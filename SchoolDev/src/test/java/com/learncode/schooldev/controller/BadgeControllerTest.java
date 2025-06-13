package com.learncode.schooldev.controller;

import com.learncode.schooldev.model.Badge;
import com.learncode.schooldev.service.BadgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeControllerTest {

    @InjectMocks
    private BadgeController badgeController;

    @Mock
    private BadgeService badgeService;

    private Badge badge1;
    private Badge badge2;

    @BeforeEach
    void setup() {
        badge1 = new Badge(1L, "Badge 1", "Description 1", "icon1.png");
        badge2 = new Badge(2L, "Badge 2", "Description 2", "icon2.png");
    }

    @Test
    void testGetAllBadges() {
        when(badgeService.getAllBadges()).thenReturn(List.of(badge1, badge2));

        List<Badge> result = badgeController.getAllBadges();

        assertEquals(2, result.size());
        assertEquals("Badge 1", result.get(0).getName());
    }

    @Test
    void testGetBadgeById_Found() {
        when(badgeService.getBadgeById(1L)).thenReturn(Optional.of(badge1));

        ResponseEntity<Badge> response = badgeController.getBadgeById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Badge 1", response.getBody().getName());
    }

    @Test
    void testGetBadgeById_NotFound() {
        when(badgeService.getBadgeById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Badge> response = badgeController.getBadgeById(99L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testCreateBadge() {
        Badge input = new Badge(null, "New", "Desc", "url.png");
        Badge saved = new Badge(3L, "New", "Desc", "url.png");

        when(badgeService.createBadge(any(Badge.class))).thenReturn(saved);

        Badge result = badgeController.createBadge(input);

        assertEquals("New", result.getName());
        assertEquals(3L, result.getBadgeId());
    }

    @Test
    void testUpdateBadge_Success() {
        Badge update = new Badge(null, "Updated", "New desc", "new.png");
        Badge updated = new Badge(1L, "Updated", "New desc", "new.png");

        when(badgeService.updateBadge(eq(1L), any(Badge.class))).thenReturn(updated);

        ResponseEntity<Badge> response = badgeController.updateBadge(1L, update);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Updated", response.getBody().getName());
    }

    @Test
    void testUpdateBadge_NotFound() {
        when(badgeService.updateBadge(eq(99L), any(Badge.class))).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<Badge> response = badgeController.updateBadge(99L, new Badge());

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testDeleteBadge() {
        doNothing().when(badgeService).deleteBadge(1L);

        ResponseEntity<Void> response = badgeController.deleteBadge(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(badgeService, times(1)).deleteBadge(1L);
    }
}
