package com.example.movielibrary.services;

import com.example.movielibrary.models.movie.Movie;
import com.example.movielibrary.models.movie.Status;
import com.example.movielibrary.models.omdb.OmdbResponseDto;
import com.example.movielibrary.repositories.MovieRepository;
import com.example.movielibrary.utils.ModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieEnrichmentServiceTests {

    @Mock
    private ExternalApiService omdbClient;

    @Mock
    private MovieRepository repository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private MovieEnrichmentService enrichmentService;

    private Movie movie;

    private static final int ID = 1;
    private static final String TITLE = "Inception";
    private static final int YEAR = 2010;

    @BeforeEach
    void setUp() {
        movie = mock(Movie.class);
    }

    @Test
    void enrichMovieAsync_ShouldReturnEarly_WhenMovieDoesNotExist() {
        when(repository.getById(ID)).thenReturn(null);

        enrichmentService.enrichMovieAsync(ID, TITLE, YEAR);

        verifyNoInteractions(omdbClient);
        verifyNoInteractions(mapper);
        verify(repository, never()).update(any());
    }

    @Test
    void enrichMovieAsync_ShouldMarkFailed_WhenOmdbResponseIsNull() {
        when(repository.getById(ID)).thenReturn(movie);
        when(omdbClient.searchMovie(TITLE, YEAR)).thenReturn(null);

        enrichmentService.enrichMovieAsync(ID, TITLE, YEAR);

        verify(movie).setStatus(Status.FAILED);
        verifyNoInteractions(mapper);
        verify(repository).update(movie);
    }

    @Test
    void enrichMovieAsync_ShouldMarkFailed_WhenOmdbResponseTitleIsNull() {
        OmdbResponseDto data = mock(OmdbResponseDto.class);
        when(data.title()).thenReturn(null);

        when(repository.getById(ID)).thenReturn(movie);
        when(omdbClient.searchMovie(TITLE, YEAR)).thenReturn(data);

        enrichmentService.enrichMovieAsync(ID, TITLE, YEAR);

        verify(movie).setStatus(Status.FAILED);
        verifyNoInteractions(mapper);
        verify(repository).update(movie);
    }

    @Test
    void enrichMovieAsync_ShouldMarkEnriched_WhenOmdbResponseIsValid() {
        OmdbResponseDto data = mock(OmdbResponseDto.class);
        Movie mappedMovie = mock(Movie.class);
        when(data.title()).thenReturn(TITLE);

        when(repository.getById(ID)).thenReturn(movie);
        when(omdbClient.searchMovie(TITLE, YEAR)).thenReturn(data);
        when(mapper.fromDto(movie, data)).thenReturn(mappedMovie);

        enrichmentService.enrichMovieAsync(ID, TITLE, YEAR);

        verify(mapper).fromDto(movie, data);
        verify(mappedMovie).setStatus(Status.ENRICHED);
        verify(movie, never()).setStatus(any());
        verify(repository).update(mappedMovie);
    }

    @Test
    void enrichMovieAsync_ShouldMarkFailed_WhenOmdbClientThrowsException() {
        when(repository.getById(ID)).thenReturn(movie);
        when(omdbClient.searchMovie(TITLE, YEAR)).thenThrow(new RuntimeException("OMDb unavailable"));

        enrichmentService.enrichMovieAsync(ID, TITLE, YEAR);

        verify(movie).setStatus(Status.FAILED);
        verifyNoInteractions(mapper);
        verify(repository).update(movie);
    }

    @Test
    void enrichMovieAsync_ShouldMarkFailed_WhenMapperThrowsException() {
        OmdbResponseDto data = mock(OmdbResponseDto.class);
        when(data.title()).thenReturn(TITLE);

        when(repository.getById(ID)).thenReturn(movie);
        when(omdbClient.searchMovie(TITLE, YEAR)).thenReturn(data);
        when(mapper.fromDto(movie, data)).thenThrow(new RuntimeException("Mapping failed"));

        enrichmentService.enrichMovieAsync(ID, TITLE, YEAR);

        verify(movie).setStatus(Status.FAILED);
        verify(repository).update(movie);
    }
}