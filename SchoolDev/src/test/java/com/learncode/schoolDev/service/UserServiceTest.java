package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.repository.UserBadgeRepository;
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

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private UserBadgeService userBadgeService;

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

    @Test
    void testGetUserBadgesByUser() {
        UserBadge ub1 = new UserBadge();
        UserBadge ub2 = new UserBadge();

        List<UserBadge> badges = Arrays.asList(ub1, ub2);
        when(userBadgeRepository.findByUser_UserId(5L)).thenReturn(badges);

        List<UserBadge> result = userBadgeService.getUserBadgesByUser(5L);

        assertEquals(2, result.size());
        verify(userBadgeRepository).findByUser_UserId(5L);
    }

    @Test
    void testGetUserBadgesByBadge() {
        UserBadge ub = new UserBadge();
        when(userBadgeRepository.findByBadge_BadgeId(8L)).thenReturn(List.of(ub));

        List<UserBadge> result = userBadgeService.getUserBadgesByBadge(8L);

        assertEquals(1, result.size());
        verify(userBadgeRepository).findByBadge_BadgeId(8L);
    }

    @Test
    void testAssignBadgeIfNotExists_assignsBadge() {
        user = new User();
        user.setUserId(11L);
        Badge badge = new Badge();
        badge.setBadgeId(22L);

        when(userBadgeRepository.existsByUser_UserIdAndBadge_BadgeId(11L, 22L)).thenReturn(false);

        userBadgeService.assignBadgeIfNotExists(user, badge);

        ArgumentCaptor<UserBadge> captor = ArgumentCaptor.forClass(UserBadge.class);
        verify(userBadgeRepository).save(captor.capture());
        UserBadge saved = captor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals(badge, saved.getBadge());
    }

    @Test
    void testAssignBadgeIfNotExists_doesNothingIfExists() {
        user = new User();
        user.setUserId(15L);
        Badge badge = new Badge();
        badge.setBadgeId(88L);

        when(userBadgeRepository.existsByUser_UserIdAndBadge_BadgeId(15L, 88L)).thenReturn(true);

        userBadgeService.assignBadgeIfNotExists(user, badge);

        verify(userBadgeRepository, never()).save(any(UserBadge.class));
    }

    @Test
    void testGetUserByUsername_Found() {
        user = new User();
        when(userRepository.findByUsername("jane")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByUsername("jane");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findByUsername("jane");
    }

    @Test
    void testGetUserByUsername_NotFound() {
        when(userRepository.findByUsername("inconnu")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByUsername("inconnu");

        assertFalse(result.isPresent());
        verify(userRepository).findByUsername("inconnu");
    }

    @Test
    void testCreateUser_EmailExists() {
        user = new User();
        user.setEmail("mail@test.com");
        user.setUsername("alex");

        when(userRepository.findByEmail("mail@test.com")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(user));
        assertTrue(ex.getMessage().contains("email existe déjà"));
        verify(userRepository).findByEmail("mail@test.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_UsernameExists() {
        user = new User();
        user.setEmail("ok@mail.com");
        user.setUsername("alex");

        when(userRepository.findByEmail("ok@mail.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("alex")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(user));
        assertTrue(ex.getMessage().contains("nom existe déjà"));
        verify(userRepository).findByEmail("ok@mail.com");
        verify(userRepository).findByUsername("alex");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("noone")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.loadUserByUsername("noone"));
        assertTrue(ex.getMessage().contains("Utilisateur non trouvé"));
        verify(userRepository).findByUsername("noone");
    }
}
