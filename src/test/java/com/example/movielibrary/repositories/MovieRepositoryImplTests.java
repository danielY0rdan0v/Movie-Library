package com.example.movielibrary.repositories;

import com.example.movielibrary.models.movie.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieRepositoryImplTests {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Movie> typedQuery;

    @InjectMocks
    private MovieRepositoryImpl movieRepository;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = mock(Movie.class);
    }

    @Test
    void getAll_ShouldReturnListOfMovies_WhenMoviesExist() {
        List<Movie> expectedMovies = List.of(movie, mock(Movie.class));

        when(entityManager.createQuery("FROM Movie", Movie.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedMovies);

        List<Movie> result = movieRepository.getAll();

        assertEquals(2, result.size());
        assertEquals(expectedMovies, result);
        verify(entityManager).createQuery("FROM Movie", Movie.class);
        verify(typedQuery).getResultList();
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoMoviesExist() {
        when(entityManager.createQuery("FROM Movie", Movie.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        List<Movie> result = movieRepository.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void getById_ShouldReturnMovie_WhenMovieExists() {
        when(entityManager.find(Movie.class, 1)).thenReturn(movie);

        Movie result = movieRepository.getById(1);

        assertNotNull(result);
        assertEquals(movie, result);
        verify(entityManager).find(Movie.class, 1);
    }

    @Test
    void getById_ShouldReturnNull_WhenMovieDoesNotExist() {
        when(entityManager.find(Movie.class, 99)).thenReturn(null);

        Movie result = movieRepository.getById(99);

        assertNull(result);
    }

    @Test
    void existsByTitle_ShouldReturnTrue_WhenMovieExists() {
        String title = "Inception";
        int year = 2010;

        when(entityManager.createQuery(
                "FROM Movie WHERE title =:title AND releaseYear = :year", Movie.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("title", title)).thenReturn(typedQuery);
        when(typedQuery.setParameter("year", year)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(movie));

        boolean result = movieRepository.existsByTitle(title, year);

        assertTrue(result);
    }

    @Test
    void existsByTitle_ShouldReturnFalse_WhenMovieDoesNotExist() {
        String title = "Nonexistent Movie";
        int year = 1999;

        when(entityManager.createQuery(
                "FROM Movie WHERE title =:title AND releaseYear = :year", Movie.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("title", title)).thenReturn(typedQuery);
        when(typedQuery.setParameter("year", year)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        boolean result = movieRepository.existsByTitle(title, year);

        assertFalse(result);
    }

    @Test
    void create_ShouldCallPersist_WithGivenMovie() {
        movieRepository.create(movie);

        verify(entityManager, times(1)).persist(movie);
    }

    @Test
    void delete_ShouldCallRemove_WithGivenMovie() {
        movieRepository.delete(movie);

        verify(entityManager, times(1)).remove(movie);
    }

    @Test
    void update_ShouldCallMerge_AndReturnMergedMovie() {
        Movie mergedMovie = mock(Movie.class);
        when(entityManager.merge(movie)).thenReturn(mergedMovie);

        Movie result = movieRepository.update(movie);

        assertNotNull(result);
        assertEquals(mergedMovie, result);
        verify(entityManager).merge(movie);
    }
}