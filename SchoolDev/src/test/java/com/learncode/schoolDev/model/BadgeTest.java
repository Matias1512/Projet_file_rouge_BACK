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
        assertNull(badge.getCurrent());
        assertNull(badge.getTotal());
        assertNull(badge.getUnlocked());
        assertNull(badge.getUnlockRequirement());
        assertNull(badge.getCreatedAt());
    }

    @Test
    void testConstructorWithoutUnlockRequirement() {
        Badge badge = new Badge(1L, "Tout feu tout flamme", "Réaliser une série de 50 jours", "fire-icon", 5, "red.500", 35, 50, true);

        assertEquals(1L, badge.getId());
        assertEquals("Tout feu tout flamme", badge.getTitle());
        assertEquals("Réaliser une série de 50 jours", badge.getDescription());
        assertEquals("fire-icon", badge.getIcon());
        assertEquals(5, badge.getLevel());
        assertEquals("red.500", badge.getColor());
        assertEquals(35, badge.getCurrent());
        assertEquals(50, badge.getTotal());
        assertTrue(badge.getUnlocked());
        assertNull(badge.getUnlockRequirement());
    }

    @Test
    void testConstructorWithUnlockRequirement() {
        Badge badge = new Badge(1L, "Éclair de génie", "Compléter 10 leçons en moins de 2 min", 
                               "bolt-icon", 3, "yellow.500", 0, 10, false, "Atteindre le niveau 5");

        assertEquals(1L, badge.getId());
        assertEquals("Éclair de génie", badge.getTitle());
        assertEquals("Compléter 10 leçons en moins de 2 min", badge.getDescription());
        assertEquals("bolt-icon", badge.getIcon());
        assertEquals(3, badge.getLevel());
        assertEquals("yellow.500", badge.getColor());
        assertEquals(0, badge.getCurrent());
        assertEquals(10, badge.getTotal());
        assertFalse(badge.getUnlocked());
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
        badge.setCurrent(3555);
        badge.setTotal(4000);
        badge.setUnlocked(true);
        badge.setUnlockRequirement("Aucune exigence");

        assertEquals(1L, badge.getId());
        assertEquals("Puits de science", badge.getTitle());
        assertEquals("Gagner 4000 XP", badge.getDescription());
        assertEquals("flask-icon", badge.getIcon());
        assertEquals(6, badge.getLevel());
        assertEquals("green.500", badge.getColor());
        assertEquals(3555, badge.getCurrent());
        assertEquals(4000, badge.getTotal());
        assertTrue(badge.getUnlocked());
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
    void testBadgeProgress() {
        Badge badge = new Badge(1L, "Spécialiste", "Apprendre 350 nouveaux mots", 
                               "file-icon", 5, "red.500", 315, 350, true);
        
        // Test du progrès
        double progressPercentage = ((double) badge.getCurrent() / badge.getTotal()) * 100;
        assertEquals(90.0, progressPercentage, 0.01);
        
        // Test si le badge est presque complet
        boolean almostComplete = badge.getCurrent() >= badge.getTotal() * 0.8;
        assertTrue(almostComplete);
        
        // Test si le badge est complet
        boolean complete = badge.getCurrent().equals(badge.getTotal());
        assertFalse(complete);
    }

    @Test
    void testLockedBadge() {
        Badge badge = new Badge(1L, "Maître linguiste", "Atteindre le niveau max dans 3 compétences", 
                               "star-icon", 7, "blue.500", 0, 3, false, "Compléter 'Spécialiste'");
        
        assertFalse(badge.getUnlocked());
        assertNotNull(badge.getUnlockRequirement());
        assertEquals("Compléter 'Spécialiste'", badge.getUnlockRequirement());
        assertEquals(0, badge.getCurrent());
    }

    @Test
    void testCompletedBadge() {
        Badge badge = new Badge(1L, "Sans-faute", "Terminer 100 leçons sans faute", 
                               "bullseye-icon", 5, "green.500", 100, 100, true);
        
        assertTrue(badge.getUnlocked());
        assertEquals(badge.getCurrent(), badge.getTotal());
        
        // Test du progrès complet
        double progressPercentage = ((double) badge.getCurrent() / badge.getTotal()) * 100;
        assertEquals(100.0, progressPercentage, 0.01);
    }
}