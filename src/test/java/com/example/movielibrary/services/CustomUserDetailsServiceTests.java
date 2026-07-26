package com.example.movielibrary.services;

import com.example.movielibrary.models.user.Role;
import com.example.movielibrary.models.user.User;
import com.example.movielibrary.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        String username = "daniel";

        when(user.getUsername()).thenReturn(username);
        when(user.getPassword()).thenReturn("encoded-password");
        when(user.getRole()).thenReturn(Role.USER);
        when(repository.getByUsername(username)).thenReturn(user);

        UserDetails result = userDetailsService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("encoded-password", result.getPassword());
        verify(repository).getByUsername(username);
    }

    @Test
    void loadUserByUsername_ShouldMapRole_WithRolePrefix() {
        String username = "admin";

        when(user.getUsername()).thenReturn(username);
        when(user.getPassword()).thenReturn("encoded-password");
        when(user.getRole()).thenReturn(Role.ADMIN);
        when(repository.getByUsername(username)).thenReturn(user);

        UserDetails result = userDetailsService.loadUserByUsername(username);

        boolean hasCorrectAuthority = result.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));

        assertEquals(1, result.getAuthorities().size());
        assertTrue(hasCorrectAuthority);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        String username = "ghost";

        when(repository.getByUsername(username)).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );

        assertTrue(exception.getMessage().contains(username));
        verify(repository).getByUsername(username);
    }
}