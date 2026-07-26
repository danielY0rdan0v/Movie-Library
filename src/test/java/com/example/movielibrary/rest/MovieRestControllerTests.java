package com.example.movielibrary.controllers.rest;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.movie.Movie;
import com.example.movielibrary.models.movie.MovieRequestDto;
import com.example.movielibrary.models.movie.MovieResponseDto;
import com.example.movielibrary.services.MovieService;
import com.example.movielibrary.utils.ModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieRestControllerTests {

    @Mock
    private MovieService service;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private MovieRestController controller;

    private Movie movie;
    private MovieResponseDto responseDto;

    private static final int ID = 1;

    @BeforeEach
    void setUp() {
        movie = mock(Movie.class);
        responseDto = mock(MovieResponseDto.class);
    }

    @Test
    void getAll_ShouldReturnListFromService() {
        List<MovieResponseDto> expected = List.of(responseDto, mock(MovieResponseDto.class));
        when(service.getAll()).thenReturn(expected);

        List<MovieResponseDto> result = controller.getAll();

        assertEquals(expected, result);
        verify(service).getAll();
    }

    @Test
    void getById_ShouldReturnDto_WhenMovieExists() {
        when(service.getById(ID)).thenReturn(movie);
        when(mapper.toDto(movie)).thenReturn(responseDto);

        MovieResponseDto result = controller.getById(ID);

        assertEquals(responseDto, result);
    }

    @Test
    void getById_ShouldThrow404_WhenMovieNotFound() {
        when(service.getById(ID)).thenThrow(new EntityNotFoundException("Movie with id " + ID + " was Not Found!"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getById(ID)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void create_ShouldReturn201WithBody_WhenMovieIsNew() {
        MovieRequestDto requestDto = mock(MovieRequestDto.class);

        when(service.create(requestDto)).thenReturn(movie);
        when(mapper.toDto(movie)).thenReturn(responseDto);

        ResponseEntity<MovieResponseDto> result = controller.create(requestDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
    }

    @Test
    void create_ShouldThrow409_WhenMovieAlreadyExists() {
        MovieRequestDto requestDto = mock(MovieRequestDto.class);

        when(service.create(requestDto)).thenThrow(new DuplicateEntityException("Movie already exists!"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.create(requestDto)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void update_ShouldReturn200WithBody_WhenMovieExists() {
        MovieRequestDto requestDto = mock(MovieRequestDto.class);
        Movie movieToUpdate = mock(Movie.class);
        Movie updatedMovie = mock(Movie.class);

        when(service.getById(ID)).thenReturn(movie);
        when(mapper.fromDto(requestDto, movie)).thenReturn(movieToUpdate);
        when(service.update(movieToUpdate)).thenReturn(updatedMovie);
        when(mapper.toDto(updatedMovie)).thenReturn(responseDto);

        ResponseEntity<MovieResponseDto> result = controller.update(ID, requestDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
    }

    @Test
    void update_ShouldThrow404_WhenMovieDoesNotExist() {
        MovieRequestDto requestDto = mock(MovieRequestDto.class);

        when(service.getById(ID)).thenThrow(new EntityNotFoundException("Movie with id " + ID + " was Not Found!"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.update(ID, requestDto)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(mapper);
        verify(service, never()).update(any());
    }

    @Test
    void delete_ShouldReturn204_WhenMovieExists() {
        ResponseEntity<Void> result = controller.delete(ID);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(service).delete(ID);
    }

    @Test
    void delete_ShouldThrow404_WhenMovieDoesNotExist() {
        doThrow(new EntityNotFoundException("Movie with id " + ID + " was Not Found!"))
                .when(service).delete(ID);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.delete(ID)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}