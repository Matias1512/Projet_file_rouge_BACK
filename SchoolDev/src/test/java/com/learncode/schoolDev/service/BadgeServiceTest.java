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
                new Badge(1L, "B1", "Desc1", "icon1.png"),
                new Badge(2L, "B2", "Desc2", "icon2.png")
        );
        when(badgeRepository.findAll()).thenReturn(badges);

        List<Badge> result = badgeService.getAllBadges();

        assertEquals(2, result.size());
        assertEquals("B1", result.get(0).getName());
        verify(badgeRepository).findAll();
    }

    @Test
    void getBadgeById_returnsBadgeIfExists() {
        Badge badge = new Badge(1L, "B1", "Desc1", "icon1.png");
        when(badgeRepository.findById(1L)).thenReturn(Optional.of(badge));

        Optional<Badge> result = badgeService.getBadgeById(1L);

        assertTrue(result.isPresent());
        assertEquals("B1", result.get().getName());
        verify(badgeRepository).findById(1L);
    }

    @Test
    void getBadgeById_returnsEmptyIfNotFound() {
        when(badgeRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Badge> result = badgeService.getBadgeById(1L);

        assertFalse(result.isPresent());
        verify(badgeRepository).findById(1L);
    }

    @Test
    void createBadge_savesBadge() {
        Badge badge = new Badge(null, "B1", "Desc1", "icon1.png");
        Badge savedBadge = new Badge(1L, "B1", "Desc1", "icon1.png");
        when(badgeRepository.save(badge)).thenReturn(savedBadge);

        Badge result = badgeService.createBadge(badge);

        assertEquals(1L, result.getBadgeId());
        verify(badgeRepository).save(badge);
    }

    @Test
    void updateBadge_updatesAndReturnsBadge() {
        Badge existing = new Badge(1L, "OldName", "OldDesc", "oldicon.png");
        Badge updates = new Badge(null, "NewName", "NewDesc", "newicon.png");
        Badge saved = new Badge(1L, "NewName", "NewDesc", "newicon.png");

        when(badgeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(badgeRepository.save(any(Badge.class))).thenReturn(saved);

        Badge result = badgeService.updateBadge(1L, updates);

        assertEquals("NewName", result.getName());
        assertEquals("NewDesc", result.getDescription());
        assertEquals("newicon.png", result.getIconUrl());
        verify(badgeRepository).findById(1L);
        verify(badgeRepository).save(existing); // C’est l’existant qui est modifié puis sauvegardé
    }

    @Test
    void updateBadge_throwsIfNotFound() {
        Badge updates = new Badge(null, "NewName", "NewDesc", "newicon.png");
        when(badgeRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> badgeService.updateBadge(1L, updates)
        );
        assertTrue(ex.getMessage().contains("Badge non trouvé"));
        verify(badgeRepository).findById(1L);
        verify(badgeRepository, never()).save(any());
    }

    @Test
    void deleteBadge_deletesById() {
        doNothing().when(badgeRepository).deleteById(1L);

        badgeService.deleteBadge(1L);

        verify(badgeRepository).deleteById(1L);
    }
}
