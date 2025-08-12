package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.service.BadgeService;
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
        badge1 = new Badge(1L, "Badge 1", "Description 1", "icon1.png", 1, "blue.500", 0, 1, true, null);
        badge2 = new Badge(2L, "Badge 2", "Description 2", "icon2.png", 1, "blue.500", 0, 1, true, null);
    }

    @Test
    void testGetAllBadges() {
        when(badgeService.getAllBadges()).thenReturn(List.of(badge1, badge2));

        List<Badge> result = badgeController.getAllBadges();

        assertEquals(2, result.size());
        assertEquals("Badge 1", result.get(0).getTitle());
    }

    @Test
    void testGetBadgeById_Found() {
        when(badgeService.getBadgeById(1L)).thenReturn(Optional.of(badge1));

        ResponseEntity<Badge> response = badgeController.getBadgeById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Badge 1", response.getBody().getTitle());
    }

    @Test
    void testGetBadgeById_NotFound() {
        when(badgeService.getBadgeById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Badge> response = badgeController.getBadgeById(99L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testCreateBadge() {
        Badge input = new Badge(null, "New", "Desc", "url.png", 1, "blue.500", 0, 1, true, null);
        Badge saved = new Badge(3L, "New", "Desc", "url.png", 1, "blue.500", 0, 1, true, null);

        when(badgeService.createBadge(any(Badge.class))).thenReturn(saved);

        Badge result = badgeController.createBadge(input);

        assertEquals("New", result.getTitle());
        assertEquals(3L, result.getId());
    }

    @Test
    void testUpdateBadge_Success() {
        Badge update = new Badge(null, "New", "Desc", "url.png", 1, "blue.500", 0, 1, true, null);
        Optional<Badge> updated = Optional.ofNullable(new Badge(1L, "Updated", "New desc", "new.png", 2, "blue.600", 1, 2, false, "New requirement"));

        when(badgeService.updateBadge(eq(1L), any(Badge.class))).thenReturn(updated);

        ResponseEntity<Optional<Badge>> response = badgeController.updateBadge(1L, update);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Updated", response.getBody().get().getTitle());
    }

    @Test
    void testUpdateBadge_NotFound() {
        when(badgeService.updateBadge(eq(99L), any(Badge.class))).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<Optional<Badge>> response = badgeController.updateBadge(99L, new Badge());

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
