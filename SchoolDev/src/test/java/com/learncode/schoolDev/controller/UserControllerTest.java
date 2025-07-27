package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

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
        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = userController.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userService).getAllUsers();
    }

    @Test
    void testGetUserById_Found() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
        verify(userService).getUserById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.getUserById(42L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(42L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userService).getUserById(42L);
    }

    @Test
    void testGetUserByEmail_Found() {
        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserByEmail("john@example.com");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
        verify(userService).getUserByEmail("john@example.com");
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userService.getUserByEmail("nobody@nowhere.com")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserByEmail("nobody@nowhere.com");

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userService).getUserByEmail("nobody@nowhere.com");
    }

    @Test
    void testCreateUser_Success() {
        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<Object> response = userController.createUser(user);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
        verify(userService).createUser(user);
    }

    @Test
    void testCreateUser_Failure() {
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("User already exists"));

        ResponseEntity<Object> response = userController.createUser(user);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("User already exists", response.getBody());
        verify(userService).createUser(user);
    }

    @Test
    void testUpdateUser_Success() {
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.updateUser(1L, user);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
        verify(userService).updateUser(1L, user);
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userService.updateUser(eq(1L), any(User.class))).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<User> response = userController.updateUser(1L, user);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userService).updateUser(1L, user);
    }

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Object> response = userController.deleteUser(1L);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userService).deleteUser(1L);
    }

    @Test
    void testDeleteUser_Failure() {
        doThrow(new RuntimeException("Cannot delete")).when(userService).deleteUser(1L);

        ResponseEntity<Object> response = userController.deleteUser(1L);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Cannot delete", response.getBody());
        verify(userService).deleteUser(1L);
    }
}
