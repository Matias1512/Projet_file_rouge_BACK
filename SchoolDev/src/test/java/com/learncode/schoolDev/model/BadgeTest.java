package com.learncode.schoolDev.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BadgeTest {

    @Test
    void testDefaultConstructor() {
        Badge badge = new Badge();

        assertNull(badge.getId());
        assertNull(badge.getTitle());
        assertNull(badge.getDescription());
        assertNull(badge.getIcon());
        assertNull(badge.getLevel());
        assertNull(badge.getColor());
        assertNull(badge.getTotal());
        assertNull(badge.getUnlockRequirement());
        assertNull(badge.getCreatedAt());
    }

    @Test
    void testConstructorWithoutUnlockRequirement() {
        Badge badge = new Badge(1L, "Tout feu tout flamme", "Réaliser une série de 50 jours", "fire-icon", 5, "red.500", 50);

        assertEquals(1L, badge.getId());
        assertEquals("Tout feu tout flamme", badge.getTitle());
        assertEquals("Réaliser une série de 50 jours", badge.getDescription());
        assertEquals("fire-icon", badge.getIcon());
        assertEquals(5, badge.getLevel());
        assertEquals("red.500", badge.getColor());
        assertEquals(50, badge.getTotal());
        assertNull(badge.getUnlockRequirement());
    }

    @Test
    void testConstructorWithUnlockRequirement() {
        Badge badge = new Badge(1L, "Éclair de génie", "Compléter 10 leçons en moins de 2 min", 
                               "bolt-icon", 3, "yellow.500", 10, "Atteindre le niveau 5");

        assertEquals(1L, badge.getId());
        assertEquals("Éclair de génie", badge.getTitle());
        assertEquals("Compléter 10 leçons en moins de 2 min", badge.getDescription());
        assertEquals("bolt-icon", badge.getIcon());
        assertEquals(3, badge.getLevel());
        assertEquals("yellow.500", badge.getColor());
        assertEquals(10, badge.getTotal());
        assertEquals("Atteindre le niveau 5", badge.getUnlockRequirement());
    }

    @Test
    void testSettersAndGetters() {
        Badge badge = new Badge();

        badge.setId(1L);
        badge.setTitle("Puits de science");
        badge.setDescription("Gagner 4000 XP");
        badge.setIcon("flask-icon");
        badge.setLevel(6);
        badge.setColor("green.500");
        badge.setTotal(4000);
        badge.setUnlockRequirement("Aucune exigence");

        assertEquals(1L, badge.getId());
        assertEquals("Puits de science", badge.getTitle());
        assertEquals("Gagner 4000 XP", badge.getDescription());
        assertEquals("flask-icon", badge.getIcon());
        assertEquals(6, badge.getLevel());
        assertEquals("green.500", badge.getColor());
        assertEquals(4000, badge.getTotal());
        assertEquals("Aucune exigence", badge.getUnlockRequirement());
    }

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

    @Test
    void testBadgeCreation() {
        Badge badge = new Badge(1L, "Spécialiste", "Apprendre 350 nouveaux mots", 
                               "file-icon", 5, "red.500", 350);
        
        assertEquals("Spécialiste", badge.getTitle());
        assertEquals("Apprendre 350 nouveaux mots", badge.getDescription());
        assertEquals("file-icon", badge.getIcon());
        assertEquals(5, badge.getLevel());
        assertEquals("red.500", badge.getColor());
        assertEquals(350, badge.getTotal());
    }

    @Test
    void testBadgeWithUnlockRequirement() {
        Badge badge = new Badge(1L, "Maître linguiste", "Atteindre le niveau max dans 3 compétences", 
                               "star-icon", 7, "blue.500", 3, "Compléter 'Spécialiste'");
        
        assertNotNull(badge.getUnlockRequirement());
        assertEquals("Compléter 'Spécialiste'", badge.getUnlockRequirement());
        assertEquals(3, badge.getTotal());
    }

    @Test
    void testSimpleBadge() {
        Badge badge = new Badge(1L, "Sans-faute", "Terminer 100 leçons sans faute", 
                               "bullseye-icon", 5, "green.500", 100);
        
        assertEquals("Sans-faute", badge.getTitle());
        assertEquals("Terminer 100 leçons sans faute", badge.getDescription());
        assertEquals("bullseye-icon", badge.getIcon());
        assertEquals(5, badge.getLevel());
        assertEquals("green.500", badge.getColor());
        assertEquals(100, badge.getTotal());
    }
}