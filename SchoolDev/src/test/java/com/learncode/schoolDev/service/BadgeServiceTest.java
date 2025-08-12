package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.repository.BadgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BadgeServiceTest {

    private BadgeRepository badgeRepository;
    private BadgeService badgeService;

    @BeforeEach
    void setUp() {
        badgeRepository = mock(BadgeRepository.class);
        badgeService = new BadgeService(badgeRepository);
    }

    @Test
    void getAllBadges_returnsAllBadges() {
        List<Badge> badges = Arrays.asList(
                new Badge(1L, "Tout feu tout flamme", "Réaliser une série de 50 jours", "fire-icon", 5, "red.500", 35, 50, true),
                new Badge(2L, "Puits de science", "Gagner 4000 XP", "flask-icon", 6, "green.500", 3555, 4000, true)
        );
        when(badgeRepository.findAll()).thenReturn(badges);

        List<Badge> result = badgeService.getAllBadges();

        assertEquals(2, result.size());
        assertEquals("Tout feu tout flamme", result.get(0).getTitle());
        verify(badgeRepository).findAll();
    }

    @Test
    void getBadgeById_returnsBadgeIfExists() {
        Badge badge = new Badge(1L, "Tout feu tout flamme", "Réaliser une série de 50 jours", "fire-icon", 5, "red.500", 35, 50, true);
        when(badgeRepository.findById(1L)).thenReturn(Optional.of(badge));

        Optional<Badge> result = badgeService.getBadgeById(1L);

        assertTrue(result.isPresent());
        assertEquals("Tout feu tout flamme", result.get().getTitle());
        verify(badgeRepository).findById(1L);
    }

    @Test
    void getBadgeById_returnsEmptyIfNotFound() {
        when(badgeRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Badge> result = badgeService.getBadgeById(99L);

        assertFalse(result.isPresent());
        verify(badgeRepository).findById(99L);
    }

    @Test
    void createBadge_savesBadge() {
        Badge badge = new Badge(1L, "Spécialiste", "Apprendre 350 nouveaux mots", "file-icon", 5, "red.500", 315, 350, true);
        Badge savedBadge = new Badge(2L, "Spécialiste", "Apprendre 350 nouveaux mots", "file-icon", 5, "red.500", 315, 350, true);
        when(badgeRepository.save(badge)).thenReturn(savedBadge);

        Badge result = badgeService.createBadge(badge);

        assertEquals(2L, result.getId());
        assertEquals("Spécialiste", result.getTitle());
        verify(badgeRepository).save(badge);
    }

    @Test
    void updateBadge_updatesAndReturnsBadge() {
        Badge existing = new Badge(1L, "Sans-faute", "Terminer 100 leçons sans faute", "bullseye-icon", 5, "green.500", 61, 100, true);
        Badge updates = new Badge(2L, "Sans-faute Updated", "Terminer 150 leçons sans faute", "bullseye-icon", 6, "blue.500", 75, 150, true);
        Badge saved = new Badge(3L, "Sans-faute Updated", "Terminer 150 leçons sans faute", "bullseye-icon", 6, "blue.500", 75, 150, true);

        when(badgeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(badgeRepository.save(any(Badge.class))).thenReturn(saved);

        Optional<Badge> result = badgeService.updateBadge(1L, updates);

        assertTrue(result.isPresent());
        assertEquals("Sans-faute Updated", result.get().getTitle());
        assertEquals("Terminer 150 leçons sans faute", result.get().getDescription());
        assertEquals("bullseye-icon", result.get().getIcon());
        assertEquals(6, result.get().getLevel());
        assertEquals("blue.500", result.get().getColor());
        assertEquals(75, result.get().getCurrent());
        assertEquals(150, result.get().getTotal());
        verify(badgeRepository).findById(1L);
        verify(badgeRepository).save(any(Badge.class));
    }

    @Test
    void updateBadge_throwsIfNotFound() {
        Badge updates = new Badge(1L, "New Title", "New Desc", "new-icon", 1, "color", 0, 10, false);
        when(badgeRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Badge> result = badgeService.updateBadge(1L, updates);
        
        assertFalse(result.isPresent());
        verify(badgeRepository).findById(1L);
        verify(badgeRepository, never()).save(any());
    }

    @Test
    void deleteBadge_deletesById() {
        doNothing().when(badgeRepository).deleteById(1L);

        badgeService.deleteBadge(1L);

        verify(badgeRepository).deleteById(1L);
    }

    @Test
    void createBadgeWithUnlockRequirement_savesBadge() {
        Badge badge = new Badge(1L, "Éclair de génie", "Compléter 10 leçons en moins de 2 min", 
                               "bolt-icon", 3, "yellow.500", 0, 10, false, "Atteindre le niveau 5");
        when(badgeRepository.save(badge)).thenReturn(badge);

        Badge result = badgeService.createBadge(badge);

        assertEquals(1L, result.getId());
        assertEquals("Éclair de génie", result.getTitle());
        assertFalse(result.getUnlocked());
        assertEquals("Atteindre le niveau 5", result.getUnlockRequirement());
        verify(badgeRepository).save(badge);
    }

    @Test
    void updateBadge_updatesAllFields() {
        Badge existing = new Badge(1L, "Maître linguiste", "Atteindre le niveau max dans 3 compétences", 
                                  "star-icon", 7, "blue.500", 0, 3, false, "Compléter 'Spécialiste'");
        Badge updates = new Badge(1L, "Grand Maître", "Atteindre le niveau max dans 5 compétences", 
                                 "crown-icon", 8, "gold.500", 2, 5, true, null);

        when(badgeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(badgeRepository.save(any(Badge.class))).thenReturn(updates);

        Optional<Badge> result = badgeService.updateBadge(1L, updates);

        assertEquals("Grand Maître", result.get().getTitle());
        assertEquals("Atteindre le niveau max dans 5 compétences", result.get().getDescription());
        assertEquals("crown-icon", result.get().getIcon());
        assertEquals(8, result.get().getLevel());
        assertEquals("gold.500", result.get().getColor());
        assertEquals(2, result.get().getCurrent());
        assertEquals(5, result.get().getTotal());
        assertTrue(result.get().getUnlocked());
        assertNull(result.get().getUnlockRequirement());
        verify(badgeRepository).findById(1L);
        verify(badgeRepository).save(existing);
    }
}