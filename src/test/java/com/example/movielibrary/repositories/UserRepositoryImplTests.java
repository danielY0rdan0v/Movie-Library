package com.example.movielibrary.repositories;

import com.example.movielibrary.models.user.User;
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
class UserRepositoryImplTests {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<User> typedQuery;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
    }

    @Test
    void getAll_ShouldReturnListOfUsers_WhenUsersExist() {
        List<User> expectedUsers = List.of(user, mock(User.class));

        when(entityManager.createQuery("FROM User", User.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedUsers);

        List<User> result = userRepository.getAll();

        assertEquals(2, result.size());
        assertEquals(expectedUsers, result);
        verify(entityManager).createQuery("FROM User", User.class);
        verify(typedQuery).getResultList();
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoUsersExist() {
        when(entityManager.createQuery("FROM User", User.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        List<User> result = userRepository.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        when(entityManager.find(User.class, 1)).thenReturn(user);

        User result = userRepository.getById(1);

        assertNotNull(result);
        assertEquals(user, result);
        verify(entityManager).find(User.class, 1);
    }

    @Test
    void getById_ShouldReturnNull_WhenUserDoesNotExist() {
        when(entityManager.find(User.class, 99)).thenReturn(null);

        User result = userRepository.getById(99);

        assertNull(result);
    }

    @Test
    void getByUsername_ShouldReturnUser_WhenUsernameExists() {
        String username = "daniel";

        when(entityManager.createQuery("FROM User WHERE username = :username", User.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(user));

        User result = userRepository.getByUsername(username);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void getByUsername_ShouldReturnNull_WhenUsernameDoesNotExist() {
        String username = "ghost";

        when(entityManager.createQuery("FROM User WHERE username = :username", User.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        User result = userRepository.getByUsername(username);

        assertNull(result);
    }

    @Test
    void isEmailExist_ShouldReturnTrue_WhenEmailExists() {
        String email = "daniel@example.com";

        when(entityManager.createQuery("FROM User WHERE email = :email", User.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("email", email)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(user));

        boolean result = userRepository.isEmailExist(email);

        assertTrue(result);
    }

    @Test
    void isEmailExist_ShouldReturnFalse_WhenEmailDoesNotExist() {
        String email = "nobody@example.com";

        when(entityManager.createQuery("FROM User WHERE email = :email", User.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("email", email)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        boolean result = userRepository.isEmailExist(email);

        assertFalse(result);
    }

    @Test
    void isUsernameExist_ShouldReturnTrue_WhenUsernameExists() {
        String username = "daniel";

        when(entityManager.createQuery("FROM User WHERE username = :username", User.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(user));

        boolean result = userRepository.isUsernameExist(username);

        assertTrue(result);
    }

    @Test
    void isUsernameExist_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        String username = "ghost";

        when(entityManager.createQuery("FROM User WHERE username = :username", User.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        boolean result = userRepository.isUsernameExist(username);

        assertFalse(result);
    }

    @Test
    void create_ShouldCallPersist_AndReturnSameUser() {
        User result = userRepository.create(user);

        verify(entityManager, times(1)).persist(user);
        assertEquals(user, result);
    }

    @Test
    void update_ShouldCallMerge_AndReturnMergedUser() {
        User mergedUser = mock(User.class);
        when(entityManager.merge(user)).thenReturn(mergedUser);

        User result = userRepository.update(user);

        assertNotNull(result);
        assertEquals(mergedUser, result);
        verify(entityManager).merge(user);
    }

    @Test
    void delete_ShouldCallRemove_WithGivenUser() {
        userRepository.delete(user);

        verify(entityManager, times(1)).remove(user);
    }
}