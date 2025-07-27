package com.learncode.schoolDev.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserBadgeTest {

    private UserBadge userBadge;
    private User user;
    private Badge badge;

    @BeforeEach
    void setUp() {
        userBadge = new UserBadge();
        
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        
        badge = new Badge();
        badge.setBadgeId(1L);
        badge.setName("Test Badge");
    }

    @Test
    void testUserGetterAndSetter() {
        userBadge.setUser(user);
        assertEquals(user, userBadge.getUser());
    }

    @Test
    void testBadgeGetterAndSetter() {
        userBadge.setBadge(badge);
        assertEquals(badge, userBadge.getBadge());
    }

    @Test
    void testUnlockedAtGetterAndSetter() {
        LocalDateTime now = LocalDateTime.now();
        userBadge.setUnlockedAt(now);
        assertEquals(now, userBadge.getUnlockedAt());
    }

    @Test
    void testOnUnlockMethod() {
        // Vérifier que unlockedAt est null avant onUnlock
        assertNull(userBadge.getUnlockedAt());
        
        LocalDateTime before = LocalDateTime.now();
        userBadge.onUnlock();
        LocalDateTime after = LocalDateTime.now();
        
        LocalDateTime unlockedAt = userBadge.getUnlockedAt();
        assertNotNull(unlockedAt);
        assertTrue(unlockedAt.isAfter(before) || unlockedAt.isEqual(before));
        assertTrue(unlockedAt.isBefore(after) || unlockedAt.isEqual(after));
    }

    @Test
    void testUserBadgeCreationWithAllFields() {
        LocalDateTime unlockedTime = LocalDateTime.now();

        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setUnlockedAt(unlockedTime);

        assertEquals(user, userBadge.getUser());
        assertEquals(badge, userBadge.getBadge());
        assertEquals(unlockedTime, userBadge.getUnlockedAt());
    }

    @Test
    void testUserBadgeDefaultValues() {
        // Vérifier les valeurs par défaut
        assertNull(userBadge.getUser());
        assertNull(userBadge.getBadge());
        assertNull(userBadge.getUnlockedAt());
    }

    @Test
    void testOnUnlockCallsSetCurrentTime() {
        // Tester que onUnlock() définit unlockedAt à l'heure actuelle
        LocalDateTime beforeUnlock = LocalDateTime.now().minusSeconds(1);
        userBadge.onUnlock();
        LocalDateTime afterUnlock = LocalDateTime.now().plusSeconds(1);
        
        LocalDateTime unlockedAt = userBadge.getUnlockedAt();
        assertNotNull(unlockedAt);
        assertTrue(unlockedAt.isAfter(beforeUnlock));
        assertTrue(unlockedAt.isBefore(afterUnlock));
    }

    // Tests pour UserBadgeKey
    @Test
    void testUserBadgeKeyDefaultConstructor() {
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey();
        assertNotNull(key);
    }

    @Test
    void testUserBadgeKeyParameterizedConstructor() {
        Long userId = 1L;
        Long badgeId = 2L;
        
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(userId, badgeId);
        assertNotNull(key);
    }

    @Test
    void testUserBadgeKeyEquals() {
        Long userId = 1L;
        Long badgeId = 2L;
        
        UserBadge.UserBadgeKey key1 = new UserBadge.UserBadgeKey(userId, badgeId);
        UserBadge.UserBadgeKey key2 = new UserBadge.UserBadgeKey(userId, badgeId);
        UserBadge.UserBadgeKey key3 = new UserBadge.UserBadgeKey(2L, 3L);
        
        // Test equality
        assertEquals(key1, key2);
        assertNotEquals(key1, key3);
        
        // Test self equality
        assertEquals(key1, key1);
        
        // Test null
        assertNotEquals(key1, null);
        
        // Test different class
        assertNotEquals(key1, "string");
    }

    @Test
    void testUserBadgeKeyHashCode() {
        Long userId = 1L;
        Long badgeId = 2L;
        
        UserBadge.UserBadgeKey key1 = new UserBadge.UserBadgeKey(userId, badgeId);
        UserBadge.UserBadgeKey key2 = new UserBadge.UserBadgeKey(userId, badgeId);
        
        // Les objets égaux doivent avoir le même hashCode
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    void testUserBadgeKeyUptdateBadgeOfAUserMethod() {
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey();
        Long userId = 1L;
        
        // Tester que la méthode ne lève pas d'exception
        assertDoesNotThrow(() -> key.uptdateBadgeOfAUser(userId));
    }

    @Test
    void testUserBadgeKeyEqualsWithNullFields() {
        UserBadge.UserBadgeKey key1 = new UserBadge.UserBadgeKey(null, null);
        UserBadge.UserBadgeKey key2 = new UserBadge.UserBadgeKey(null, null);
        UserBadge.UserBadgeKey key3 = new UserBadge.UserBadgeKey(1L, null);
        
        assertEquals(key1, key2);
        assertNotEquals(key1, key3);
    }

    @Test
    void testMultipleOnUnlockCalls() {
        // Premier appel
        userBadge.onUnlock();
        LocalDateTime firstUnlock = userBadge.getUnlockedAt();
        assertNotNull(firstUnlock);
        
        // Deuxième appel immédiat
        userBadge.onUnlock();
        LocalDateTime secondUnlock = userBadge.getUnlockedAt();
        
        // Le timestamp doit être égal ou après le premier
        assertTrue(secondUnlock.isAfter(firstUnlock) || secondUnlock.isEqual(firstUnlock));
    }
}