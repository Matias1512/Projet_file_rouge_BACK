package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(user, new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(42L);

        assertFalse(result.isPresent());
        verify(userRepository).findById(42L);
    }

    @Test
    void testGetUserByEmail_Found() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail("john@example.com");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail("nobody@nowhere.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("nobody@nowhere.com");

        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("nobody@nowhere.com");
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User input = new User();
        input.setUsername("johndoe");
        input.setEmail("john@example.com");

        User result = userService.createUser(input);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(userRepository).save(input);
    }

    @Test
    void testCreateUser_Failure() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Duplicate email"));

        User input = new User();
        input.setUsername("johndoe");
        input.setEmail("john@example.com");

        Exception ex = assertThrows(RuntimeException.class, () -> userService.createUser(input));
        assertEquals("Duplicate email", ex.getMessage());
        verify(userRepository).save(input);
    }

    @Test
    void testUpdateUser_Found() {
        User updated = new User();
        updated.setUserId(1L);
        updated.setUsername("janedoe");
        updated.setEmail("jane@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        User result = userService.updateUser(1L, updated);

        assertNotNull(result);
        assertEquals("janedoe", result.getUsername());
        assertEquals("jane@example.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        User updated = new User();
        updated.setUserId(42L);

        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> userService.updateUser(42L, updated));
        assertEquals("Utilisateur non trouvé avec ID : 42", ex.getMessage());
        verify(userRepository).findById(42L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.existsById(42L)).thenReturn(false);
    
        Exception ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(42L));
        assertTrue(ex.getMessage().toLowerCase().contains("non trouvé"));
        verify(userRepository, never()).deleteById(any());
    }
}
