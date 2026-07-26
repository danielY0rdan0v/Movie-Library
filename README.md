# Movie-Library

## 1. External API

The project integrates with the Omdb API
to enrich locally created movie records
with authoritative metadata (title, director, rating) 
sourced from IMDb-derived data

OMDb was chosen over alternatives TMDb 
because of a flat query-parameter interface (`t` for title, `y` for year),
and a response schema that maps cleanly onto a small, purpose-built DTO (`OmdbResponseDto`).
This keeps the integration surface minimal
— a single `ExternalApiService` wraps a Spring `RestClient` and exposes one method,
`searchMovie(title, year)`,
so the rest of the application depends only on a narrow,
testable contract rather than the OMDb client directly

The API key is externalized via `@Value("${omdb.api.key}")`,
keeping credentials out of source control and allowing per-environment configuration.

## 2. Authentication & Authorization

Authentication is handled with **Spring Security using HTTP Basic Auth** (`httpBasic()`),
backed by a custom `UserDetailsService` (`CustomUserDetailsService`)
that loads users from the application's own `User` table rather than an in-memory store.
Passwords are hashed with **BCrypt** (`BCryptPasswordEncoder`) —
both on user creation and on password updates —
so plaintext credentials are never persisted.

- Swagger/OpenAPI endpoints and user registration (`POST /api/users`) are public, to allow account creation and API exploration without prior credentials.
- Read access to movies (`GET /api/movies/**`) is open to both `USER` and `ADMIN` roles.
- Write access to movies, and all `/api/users/**` endpoints, are restricted to `ADMIN`.
- Everything else defaults to `authenticated()`.

Custom `authenticationEntryPoint` and `accessDeniedHandler` implementations
return plain-text 403 responses with human-readable messages,
favoring clarity for API consumers over strict
adherence to the 401/403 semantic split (unauthenticated requests also receive 403 rather than 401).

## 3. Asynchronous Enrichment

Movie creation and OMDb enrichment are **decoupled** 
to avoid blocking the client on a third-party network call.
The flow is:

1. `MovieServiceImpl.create()` persists the movie record synchronously (with an initial status)
and returns immediately.
2. It then triggers `MovieEnrichmentService.enrichMovieAsync(id, title, year)`,
annotated `@Async("taskExecutor")`, which runs on a dedicated thread pool rather than the request thread.
3. The async method re-fetches the movie by ID, calls `ExternalApiService.searchMovie(...)`,
and on success maps the OMDb response onto the entity via `ModelMapper`, marking it `Status.ENRICHED`. If OMDb returns no match (`null` or missing title) or the call throws,
the movie is marked `Status.FAILED` instead — the enrichment step never allows a failure to bubble up and break movie creation itself.
4. The final state is persisted via `repository.update(...)`.

This design trades **immediate consistency** for **availability and responsiveness**:
the client receives a `201 Created` response before enrichment completes,
and must poll (or otherwise re-fetch) the movie to observe its final
`ENRICHED`/`FAILED` status. This is an acceptable trade-off given OMDb
is a non-critical, best-effort enhancement rather than core domain data.

## 4. API Endpoints

### Users — `/api/users`

| Method | Path | Description |
|---|---|---|
| GET | `/api/users` | Returns all users. Requires authentication. |
| GET | `/api/users/{id}` | Returns a single user by ID, or 404 if not found. Requires authentication. |
| POST | `/api/users` | Registers a new user; encodes the password and rejects duplicate emails/usernames with 409. Public — no authentication required. |
| PUT | `/api/users/{id}` | Updates first/last name and, optionally, password for an existing user; 404 if the user doesn't exist. Requires authentication. |
| DELETE | `/api/users/{id}` | Deletes a user by ID, or 404 if not found. Requires authentication. |

### Movies — `/api/movies`

| Method | Path | Description |
|---|---|---|
| GET | `/api/movies` | Returns all movies. Requires `USER` or `ADMIN` role. |
| GET | `/api/movies/{id}` | Returns a single movie by ID, or 404 if not found. Requires `USER` or `ADMIN` role. |
| POST | `/api/movies` | Creates a movie; rejects duplicate title+year with 409; asynchronously triggers OMDb enrichment in the background. Requires `ADMIN` role. |
| PUT | `/api/movies/{id}` | Updates an existing movie's fields, or 404 if not found. Requires `ADMIN` role. |
| DELETE | `/api/movies/{id}` | Deletes a movie by ID, or 404 if not found. Requires `ADMIN` role. |

## 5. Architectural Decisions & Trade-offs

- **`EntityManager` + raw JPQL over Spring Data JPA repositories.**
Repository implementations (`MovieRepositoryImpl`, `UserRepositoryImpl`)
use `EntityManager` directly with hand-written JPQL.
This trades boilerplate and some compile-time safety for explicit control over query shape —
useful for `COUNT`/`EXISTS`-style existence checks (`existsByTitle`, `isEmailExist`)
that avoid loading full entity graphs.
- **Manual, dependency-free `ModelMapper` utility**. Keeps mapping logic explicit and 
debuggable at the cost of more hand-written boilerplate per entity/DTO pair.
- **Centralized exception translation** via `ResponseStatusException`
at the controller layer keeps the HTTP-status decision colocated
with each endpoint, at the cost of some repetition across controllers.
- **Status field (`Status.FAILED`/`ENRICHED`) as the mechanism 
