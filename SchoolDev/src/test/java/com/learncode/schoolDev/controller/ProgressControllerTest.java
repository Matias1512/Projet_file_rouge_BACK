package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.model.Progress;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.Course;
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

    @Test
    void testUpdateProgressAutomatically_Success() {
        Progress progress = createProgress(20L);
        when(progressService.updateProgressAutomatically(1L, 2L)).thenReturn(progress);

        ResponseEntity<Progress> response = progressController.updateProgressAutomatically(1L, 2L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(progress, response.getBody());
        verify(progressService).updateProgressAutomatically(1L, 2L);
    }

    @Test
    void testUpdateProgressAutomatically_Error() {
        when(progressService.updateProgressAutomatically(999L, 999L))
            .thenThrow(new RuntimeException("User or course not found"));

        ResponseEntity<Progress> response = progressController.updateProgressAutomatically(999L, 999L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testInitializeProgressForCourse_Success() {
        Progress progress = createProgress(30L);
        when(progressService.initializeProgressForCourse(3L, 4L)).thenReturn(progress);

        ResponseEntity<Progress> response = progressController.initializeProgressForCourse(3L, 4L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(progress, response.getBody());
        verify(progressService).initializeProgressForCourse(3L, 4L);
    }

    @Test
    void testInitializeProgressForCourse_Error() {
        when(progressService.initializeProgressForCourse(888L, 888L))
            .thenThrow(new RuntimeException("User or course not found"));

        ResponseEntity<Progress> response = progressController.initializeProgressForCourse(888L, 888L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testCalculateProgressPercentage_Success() {
        when(progressService.calculateProgressPercentage(5L, 6L)).thenReturn(75.5);

        ResponseEntity<Double> response = progressController.calculateProgressPercentage(5L, 6L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(75.5, response.getBody());
        verify(progressService).calculateProgressPercentage(5L, 6L);
    }

    @Test
    void testCalculateProgressPercentage_Error() {
        when(progressService.calculateProgressPercentage(777L, 777L))
            .thenThrow(new RuntimeException("Progress not found"));

        ResponseEntity<Double> response = progressController.calculateProgressPercentage(777L, 777L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testIsCourseCompleted_True() {
        when(progressService.isCourseCompleted(7L, 8L)).thenReturn(true);

        ResponseEntity<Boolean> response = progressController.isCourseCompleted(7L, 8L);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody());
        verify(progressService).isCourseCompleted(7L, 8L);
    }

    @Test
    void testIsCourseCompleted_False() {
        when(progressService.isCourseCompleted(9L, 10L)).thenReturn(false);

        ResponseEntity<Boolean> response = progressController.isCourseCompleted(9L, 10L);

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody());
        verify(progressService).isCourseCompleted(9L, 10L);
    }

    @Test
    void testIsCourseCompleted_Error() {
        when(progressService.isCourseCompleted(666L, 666L))
            .thenThrow(new RuntimeException("Progress not found"));

        ResponseEntity<Boolean> response = progressController.isCourseCompleted(666L, 666L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testGetCurrentLessonId_Success() {
        when(progressService.getCurrentLessonId(11L, 12L)).thenReturn(25L);

        ResponseEntity<Long> response = progressController.getCurrentLessonId(11L, 12L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(25L, response.getBody());
        verify(progressService).getCurrentLessonId(11L, 12L);
    }

    @Test
    void testGetCurrentLessonId_Error() {
        when(progressService.getCurrentLessonId(555L, 555L))
            .thenThrow(new RuntimeException("Progress not found"));

        ResponseEntity<Long> response = progressController.getCurrentLessonId(555L, 555L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testWithNegativeIds() {
        when(progressService.getProgressById(-1L)).thenReturn(Optional.empty());

        ResponseEntity<Progress> response = progressController.getProgressById(-1L);

        assertEquals(404, response.getStatusCode().value());
        verify(progressService).getProgressById(-1L);
    }

    @Test
    void testWithZeroIds() {
        when(progressService.getProgressById(0L)).thenReturn(Optional.empty());

        ResponseEntity<Progress> response = progressController.getProgressById(0L);

        assertEquals(404, response.getStatusCode().value());
        verify(progressService).getProgressById(0L);
    }

    @Test
    void testWithLargeIds() {
        Long largeId = Long.MAX_VALUE;
        when(progressService.getProgressById(largeId)).thenReturn(Optional.empty());

        ResponseEntity<Progress> response = progressController.getProgressById(largeId);

        assertEquals(404, response.getStatusCode().value());
        verify(progressService).getProgressById(largeId);
    }

    @Test
    void testMultipleCalls() {
        Progress p1 = createProgress(100L);
        Progress p2 = createProgress(200L);
        
        when(progressService.getProgressById(100L)).thenReturn(Optional.of(p1));
        when(progressService.getProgressById(200L)).thenReturn(Optional.of(p2));

        ResponseEntity<Progress> response1 = progressController.getProgressById(100L);
        ResponseEntity<Progress> response2 = progressController.getProgressById(200L);

        assertEquals(200, response1.getStatusCode().value());
        assertEquals(100L, response1.getBody().getProgressId());
        assertEquals(200, response2.getStatusCode().value());
        assertEquals(200L, response2.getBody().getProgressId());
        
        verify(progressService).getProgressById(100L);
        verify(progressService).getProgressById(200L);
    }

    // Méthode utilitaire pour créer un Progress
    private Progress createProgress(Long id) {
        Progress progress = new Progress();
        progress.setProgressId(id);
        progress.setCurrentLessonId(1L);
        progress.setPercentageCompleted(0.0);
        
        // Créer des objets User et Course simplifiés pour les tests
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        progress.setUser(user);
        
        Course course = new Course();
        course.setCourseId(1L);
        course.setTitle("Test Course");
        progress.setCourse(course);
        
        return progress;
    }
}
