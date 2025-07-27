package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.model.Progress;
import com.learncode.schoolDev.service.ProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProgressControllerTest {

    @Mock
    private ProgressService progressService;

    @InjectMocks
    private ProgressController progressController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProgress() {
        Progress p1 = createProgress(1L);
        Progress p2 = createProgress(2L);
        when(progressService.getAllProgress()).thenReturn(Arrays.asList(p1, p2));

        List<Progress> result = progressController.getAllProgress();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getProgressId());
        assertEquals(2L, result.get(1).getProgressId());
        verify(progressService).getAllProgress();
    }

    @Test
    void testGetProgressById_Found() {
        Progress p = createProgress(10L);
        when(progressService.getProgressById(10L)).thenReturn(Optional.of(p));

        ResponseEntity<Progress> response = progressController.getProgressById(10L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(p, response.getBody());
    }

    @Test
    void testGetProgressById_NotFound() {
        when(progressService.getProgressById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Progress> response = progressController.getProgressById(99L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testGetProgressByUser() {
        Progress p = createProgress(5L);
        when(progressService.getProgressByUser(77L)).thenReturn(List.of(p));

        List<Progress> result = progressController.getProgressByUser(77L);

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getProgressId());
        verify(progressService).getProgressByUser(77L);
    }

    @Test
    void testGetProgressByCourse() {
        Progress p = createProgress(3L);
        when(progressService.getProgressByCourse(55L)).thenReturn(List.of(p));

        List<Progress> result = progressController.getProgressByCourse(55L);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getProgressId());
        verify(progressService).getProgressByCourse(55L);
    }

    @Test
    void testCreateProgress() {
        Progress input = createProgress(null);
        Progress saved = createProgress(12L);
        when(progressService.createProgress(input)).thenReturn(saved);

        Progress result = progressController.createProgress(input);

        assertEquals(saved, result);
        verify(progressService).createProgress(input);
    }

    @Test
    void testUpdateProgress_Success() {
        Progress updated = createProgress(8L);
        when(progressService.updateProgress(8L, updated)).thenReturn(updated);

        ResponseEntity<Progress> response = progressController.updateProgress(8L, updated);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updated, response.getBody());
    }

    @Test
    void testUpdateProgress_NotFound() {
        Progress p = createProgress(77L);
        when(progressService.updateProgress(eq(77L), any())).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<Progress> response = progressController.updateProgress(77L, p);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteProgress() {
        doNothing().when(progressService).deleteProgress(15L);

        ResponseEntity<Void> response = progressController.deleteProgress(15L);

        assertEquals(204, response.getStatusCode().value());
        verify(progressService).deleteProgress(15L);
    }

    // Méthode utilitaire pour créer un Progress
    private Progress createProgress(Long id) {
        Progress progress = new Progress();
        progress.setProgressId(id);
        // Ajoute d'autres setters si besoin
        return progress;
    }
}
