package com.example.movielibrary.controllers.rest;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.user.UpdateUserRequestDto;
import com.example.movielibrary.models.user.User;
import com.example.movielibrary.models.user.UserRequestDto;
import com.example.movielibrary.models.user.UserResponseDto;
import com.example.movielibrary.services.UserService;
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
class UserRestControllerTests {

    @Mock
    private UserService service;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserRestController controller;

    private User user;
    private UserResponseDto responseDto;

    private static final int ID = 1;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        responseDto = mock(UserResponseDto.class);
    }

    @Test
    void getAll_ShouldReturnListFromService() {
        List<UserResponseDto> expected = List.of(responseDto, mock(UserResponseDto.class));
        when(service.getAll()).thenReturn(expected);

        List<UserResponseDto> result = controller.getAll();

        assertEquals(expected, result);
        verify(service).getAll();
    }

    @Test
    void getById_ShouldReturnDto_WhenUserExists() {
        when(service.getById(ID)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = controller.getById(ID);

        assertEquals(responseDto, result);
    }

    @Test
    void getById_ShouldThrow404_WhenUserNotFound() {
        when(service.getById(ID)).thenThrow(new EntityNotFoundException("User with id " + ID + " was not found!"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getById(ID)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void create_ShouldReturn201WithBody_WhenUserIsValid() {
        UserRequestDto requestDto = mock(UserRequestDto.class);

        when(mapper.fromDto(requestDto)).thenReturn(user);
        when(service.create(user)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(responseDto);

        ResponseEntity<UserResponseDto> result = controller.create(requestDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
    }

    @Test
    void create_ShouldThrow409_WhenUserAlreadyExists() {
        UserRequestDto requestDto = mock(UserRequestDto.class);

        when(mapper.fromDto(requestDto)).thenReturn(user);
        when(service.create(user)).thenThrow(new DuplicateEntityException("User already exists!"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.create(requestDto)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void update_ShouldReturn200WithBody_WhenUserExists() {
        UpdateUserRequestDto requestDto = mock(UpdateUserRequestDto.class);
        User updatedUser = mock(User.class);

        when(mapper.fromDtoUpdate(requestDto)).thenReturn(user);
        when(service.update(user, ID)).thenReturn(updatedUser);
        when(mapper.toDto(updatedUser)).thenReturn(responseDto);

        ResponseEntity<UserResponseDto> result = controller.update(ID, requestDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
    }

    @Test
    void update_ShouldThrow404_WhenUserDoesNotExist() {
        UpdateUserRequestDto requestDto = mock(UpdateUserRequestDto.class);

        when(mapper.fromDtoUpdate(requestDto)).thenReturn(user);
        when(service.update(user, ID)).thenThrow(new EntityNotFoundException("User with id " + ID + " was not found!"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.update(ID, requestDto)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void delete_ShouldReturn204_WhenUserExists() {
        ResponseEntity<Void> result = controller.delete(ID);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(service).delete(ID);
    }

    @Test
    void delete_ShouldThrow404_WhenUserDoesNotExist() {
        doThrow(new EntityNotFoundException("User with id " + ID + " was not found!"))
                .when(service).delete(ID);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.delete(ID)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}