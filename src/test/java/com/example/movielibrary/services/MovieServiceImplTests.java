package com.example.movielibrary.services;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.movie.Movie;
import com.example.movielibrary.models.movie.MovieRequestDto;
import com.example.movielibrary.models.movie.MovieResponseDto;
import com.example.movielibrary.repositories.MovieRepository;
import com.example.movielibrary.utils.ModelMapper;
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
class MovieServiceImplTests {

    @Mock
    private MovieRepository repository;

    @Mock
    private MovieEnrichmentService enrichmentService;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie;

    private static final int ID = 1;
    private static final String TITLE = "Inception";
    private static final int YEAR = 2010;

    @BeforeEach
    void setUp() {
        movie = mock(Movie.class);
    }

    @Test
    void getAll_ShouldReturnMappedDtoList_WhenMoviesExist() {
        Movie movie2 = mock(Movie.class);
        MovieResponseDto dto1 = mock(MovieResponseDto.class);
        MovieResponseDto dto2 = mock(MovieResponseDto.class);

        when(repository.getAll()).thenReturn(List.of(movie, movie2));
        when(mapper.toDto(movie)).thenReturn(dto1);
        when(mapper.toDto(movie2)).thenReturn(dto2);

        List<MovieResponseDto> result = movieService.getAll();

        assertEquals(2, result.size());
        assertEquals(List.of(dto1, dto2), result);
        verify(mapper).toDto(movie);
        verify(mapper).toDto(movie2);
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoMoviesExist() {
        when(repository.getAll()).thenReturn(List.of());

        List<MovieResponseDto> result = movieService.getAll();

        assertTrue(result.isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void getById_ShouldReturnMovie_WhenMovieExists() {
        when(repository.getById(ID)).thenReturn(movie);

        Movie result = movieService.getById(ID);

        assertEquals(movie, result);
    }

    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenMovieDoesNotExist() {
        when(repository.getById(ID)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> movieService.getById(ID)
        );

        assertTrue(exception.getMessage().contains(String.valueOf(ID)));
    }

    @Test
    void create_ShouldPersistAndTriggerEnrichment_WhenMovieDoesNotExist() {
        MovieRequestDto requestDto = mock(MovieRequestDto.class);

        when(mapper.fromDto(requestDto)).thenReturn(movie);
        when(movie.getTitle()).thenReturn(TITLE);
        when(movie.getReleaseYear()).thenReturn(YEAR);
        when(movie.getId()).thenReturn(ID);
        when(repository.existsByTitle(TITLE, YEAR)).thenReturn(false);

        Movie result = movieService.create(requestDto);

        assertEquals(movie, result);
        verify(repository).create(movie);
        verify(enrichmentService).enrichMovieAsync(ID, TITLE, YEAR);
    }

    @Test
    void create_ShouldThrowDuplicateEntityException_WhenMovieAlreadyExists() {
        MovieRequestDto requestDto = mock(MovieRequestDto.class);

        when(mapper.fromDto(requestDto)).thenReturn(movie);
        when(movie.getTitle()).thenReturn(TITLE);
        when(movie.getReleaseYear()).thenReturn(YEAR);
        when(requestDto.getTitle()).thenReturn(TITLE);
        when(repository.existsByTitle(TITLE, YEAR)).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> movieService.create(requestDto));

        verify(repository, never()).create(any());
        verifyNoInteractions(enrichmentService);
    }

    @Test
    void update_ShouldDelegateToRepository_AndReturnUpdatedMovie() {
        Movie updatedMovie = mock(Movie.class);
        when(repository.update(movie)).thenReturn(updatedMovie);

        Movie result = movieService.update(movie);

        assertEquals(updatedMovie, result);
        verify(repository).update(movie);
    }

    @Test
    void delete_ShouldRemoveMovie_WhenMovieExists() {
        when(repository.getById(ID)).thenReturn(movie);

        movieService.delete(ID);

        verify(repository).delete(movie);
    }

    @Test
    void delete_ShouldThrowEntityNotFoundException_WhenMovieDoesNotExist() {
        when(repository.getById(ID)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> movieService.delete(ID));

        verify(repository, never()).delete(any());
    }
}