package com.example.movielibrary.services;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.user.User;
import com.example.movielibrary.models.user.UserResponseDto;
import com.example.movielibrary.repositories.UserRepository;
import com.example.movielibrary.utils.ModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    private static final int ID = 1;
    private static final String EMAIL = "daniel@example.com";
    private static final String USERNAME = "daniel";
    private static final String RAW_PASSWORD = "raw-password";
    private static final String ENCODED_PASSWORD = "encoded-password";

    @BeforeEach
    void setUp() {
        user = mock(User.class);
    }

    @Test
    void getAll_ShouldReturnMappedDtoList_WhenUsersExist() {
        User user2 = mock(User.class);
        UserResponseDto dto1 = mock(UserResponseDto.class);
        UserResponseDto dto2 = mock(UserResponseDto.class);

        when(repository.getAll()).thenReturn(List.of(user, user2));
        when(mapper.toDto(user)).thenReturn(dto1);
        when(mapper.toDto(user2)).thenReturn(dto2);

        List<UserResponseDto> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals(List.of(dto1, dto2), result);
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoUsersExist() {
        when(repository.getAll()).thenReturn(List.of());

        List<UserResponseDto> result = userService.getAll();

        assertTrue(result.isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        when(repository.getById(ID)).thenReturn(user);

        User result = userService.getById(ID);

        assertEquals(user, result);
    }

    @Test
    void getById_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
        when(repository.getById(ID)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getById(ID)
        );

        assertTrue(exception.getMessage().contains(String.valueOf(ID)));
    }

    @Test
    void create_ShouldEncodePasswordAndPersist_WhenUserDoesNotExist() {
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getUsername()).thenReturn(USERNAME);
        when(user.getPassword()).thenReturn(RAW_PASSWORD);
        when(repository.isEmailExist(EMAIL)).thenReturn(false);
        when(repository.isUsernameExist(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(repository.create(user)).thenReturn(user);

        User result = userService.create(user);

        assertEquals(user, result);
        verify(user).setPassword(ENCODED_PASSWORD);
        verify(repository).create(user);
    }

    @Test
    void create_ShouldThrowDuplicateEntityException_WhenEmailAlreadyExists() {
        when(user.getEmail()).thenReturn(EMAIL);
        when(repository.isEmailExist(EMAIL)).thenReturn(true);

        DuplicateEntityException exception = assertThrows(
                DuplicateEntityException.class,
                () -> userService.create(user)
        );

        assertTrue(exception.getMessage().contains(EMAIL));
        verify(repository, never()).isUsernameExist(any());
        verify(passwordEncoder, never()).encode(any());
        verify(repository, never()).create(any());
    }

    @Test
    void create_ShouldThrowDuplicateEntityException_WhenUsernameAlreadyExists() {
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getUsername()).thenReturn(USERNAME);
        when(repository.isEmailExist(EMAIL)).thenReturn(false);
        when(repository.isUsernameExist(USERNAME)).thenReturn(true);

        DuplicateEntityException exception = assertThrows(
                DuplicateEntityException.class,
                () -> userService.create(user)
        );

        assertTrue(exception.getMessage().contains(USERNAME));
        verify(passwordEncoder, never()).encode(any());
        verify(repository, never()).create(any());
    }

    @Test
    void update_ShouldUpdateUser_WhenUserExists() {
        User updatedUser = mock(User.class);
        when(repository.getById(ID)).thenReturn(user);
        when(repository.update(user)).thenReturn(updatedUser);

        User result = userService.update(user, ID);

        assertEquals(updatedUser, result);
        verify(repository).update(user);
    }

    @Test
    void update_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
        when(repository.getById(ID)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.update(user, ID));

        verify(repository, never()).update(any());
    }

    @Test
    void delete_ShouldRemoveUser_WhenUserExists() {
        when(repository.getById(ID)).thenReturn(user);

        userService.delete(ID);

        verify(repository).delete(user);
    }

    @Test
    void delete_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
        when(repository.getById(ID)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.delete(ID));

        verify(repository, never()).delete(any());
    }
}