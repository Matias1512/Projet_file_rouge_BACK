package com.learncode.schoolDev.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserIdGetterAndSetter() {
        Long userId = 1L;
        user.setUserId(userId);
        assertEquals(userId, user.getUserId());
    }

    @Test
    void testUsernameGetterAndSetter() {
        String username = "testuser";
        user.setUsername(username);
        assertEquals(username, user.getUsername());
    }

    @Test
    void testEmailGetterAndSetter() {
        String email = "test@example.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    @Test
    void testPasswordHashGetterAndSetter() {
        String passwordHash = "hashedPassword123";
        user.setPasswordHash(passwordHash);
        assertEquals(passwordHash, user.getPasswordHash());
    }

    @Test
    void testRoleGetterAndSetter() {
        String role = "USER";
        user.setRole(role);
        assertEquals(role, user.getRole());
    }

    @Test
    void testSignupDateGetter() {
        // Initialement null
        assertNull(user.getSignupDate());
        
        // Après appel de onCreate()
        user.onCreate();
        assertNotNull(user.getSignupDate());
        assertTrue(user.getSignupDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testUpdatedAtGetterAndSetter() {
        LocalDateTime now = LocalDateTime.now();
        user.setUpdatedAt(now);
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testOnCreateMethod() {
        // Vérifier que signupDate est null avant onCreate
        assertNull(user.getSignupDate());
        
        LocalDateTime before = LocalDateTime.now();
        user.onCreate();
        LocalDateTime after = LocalDateTime.now();
        
        LocalDateTime signupDate = user.getSignupDate();
        assertNotNull(signupDate);
        assertTrue(signupDate.isAfter(before) || signupDate.isEqual(before));
        assertTrue(signupDate.isBefore(after) || signupDate.isEqual(after));
    }

    @Test
    void testUserCreationWithAllFields() {
        Long userId = 1L;
        String username = "testuser";
        String email = "test@example.com";
        String passwordHash = "hashedPassword123";
        String role = "ADMIN";
        LocalDateTime updatedAt = LocalDateTime.now();

        user.setUserId(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setUpdatedAt(updatedAt);
        user.onCreate();

        assertEquals(userId, user.getUserId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals(role, user.getRole());
        assertEquals(updatedAt, user.getUpdatedAt());
        assertNotNull(user.getSignupDate());
    }

    @Test
    void testUserDefaultValues() {
        // Vérifier les valeurs par défaut
        assertNull(user.getUserId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPasswordHash());
        assertNull(user.getRole());
        assertNull(user.getSignupDate());
        assertNull(user.getUpdatedAt());
    }

    @Test
    void testOnCreateCallsSetCurrentTime() {
        // Tester que onCreate() définit signupDate à l'heure actuelle
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
        user.onCreate();
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);
        
        LocalDateTime signupDate = user.getSignupDate();
        assertNotNull(signupDate);
        assertTrue(signupDate.isAfter(beforeCreate));
        assertTrue(signupDate.isBefore(afterCreate));
    }
}