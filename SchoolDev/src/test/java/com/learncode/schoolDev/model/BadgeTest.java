package com.learncode.schoolDev.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BadgeTest {

    @Test
    void testOnCreateAndGetCreatedAt() {
        Badge badge = new Badge();

        // Avant l'appel, createdAt doit être null
        assertNull(badge.getCreatedAt());

        // On simule l'appel PrePersist
        badge.onCreate();

        // Après l'appel, createdAt ne doit plus être null
        assertNotNull(badge.getCreatedAt());

        // Optionnel : vérifie que la date est proche de maintenant
        assertTrue(badge.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(2)));
    }
}