package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.Progress;
import com.learncode.schoolDev.repository.ProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProgressServiceTest {

    @Mock
    private ProgressRepository progressRepository;

    @InjectMocks
    private ProgressService progressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProgress() {
        Progress p1 = new Progress();
        Progress p2 = new Progress();
        when(progressRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Progress> result = progressService.getAllProgress();

        assertEquals(2, result.size());
        verify(progressRepository).findAll();
    }

    @Test
    void testGetProgressById_Found() {
        Progress p = new Progress();
        when(progressRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<Progress> result = progressService.getProgressById(1L);

        assertTrue(result.isPresent());
        assertEquals(p, result.get());
        verify(progressRepository).findById(1L);
    }

    @Test
    void testGetProgressById_NotFound() {
        when(progressRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Progress> result = progressService.getProgressById(99L);

        assertFalse(result.isPresent());
        verify(progressRepository).findById(99L);
    }

    @Test
    void testGetProgressByUser() {
        Progress p = new Progress();
        when(progressRepository.findByUser_UserId(42L)).thenReturn(List.of(p));

        List<Progress> result = progressService.getProgressByUser(42L);

        assertEquals(1, result.size());
        verify(progressRepository).findByUser_UserId(42L);
    }

    @Test
    void testGetProgressByCourse() {
        Progress p = new Progress();
        when(progressRepository.findByCourse_CourseId(77L)).thenReturn(List.of(p));

        List<Progress> result = progressService.getProgressByCourse(77L);

        assertEquals(1, result.size());
        verify(progressRepository).findByCourse_CourseId(77L);
    }

    @Test
    void testGetProgressByUserAndCourse_Found() {
        Progress p = new Progress();
        when(progressRepository.findByUser_UserIdAndCourse_CourseId(5L, 9L)).thenReturn(Optional.of(p));

        Optional<Progress> result = progressService.getProgressByUserAndCourse(5L, 9L);

        assertTrue(result.isPresent());
        assertEquals(p, result.get());
        verify(progressRepository).findByUser_UserIdAndCourse_CourseId(5L, 9L);
    }

    @Test
    void testGetProgressByUserAndCourse_NotFound() {
        when(progressRepository.findByUser_UserIdAndCourse_CourseId(1L, 2L)).thenReturn(Optional.empty());

        Optional<Progress> result = progressService.getProgressByUserAndCourse(1L, 2L);

        assertFalse(result.isPresent());
        verify(progressRepository).findByUser_UserIdAndCourse_CourseId(1L, 2L);
    }

    @Test
    void testCreateProgress() {
        Progress p = new Progress();
        when(progressRepository.save(p)).thenReturn(p);

        Progress result = progressService.createProgress(p);

        assertEquals(p, result);
        verify(progressRepository).save(p);
    }

    @Test
    void testUpdateProgress_Found() {
        Progress existing = new Progress();
        existing.setCurrentLessonId(1L);
        existing.setPercentageCompleted(0.2);
        Progress updated = new Progress();
        updated.setCurrentLessonId(2L);
        updated.setPercentageCompleted(0.9);

        when(progressRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(progressRepository.save(existing)).thenReturn(existing);

        Progress result = progressService.updateProgress(5L, updated);

        assertEquals(2L, result.getCurrentLessonId());
        assertEquals(0.9, result.getPercentageCompleted());
        verify(progressRepository).findById(5L);
        verify(progressRepository).save(existing);
    }

    @Test
    void testUpdateProgress_NotFound() {
        Progress updated = new Progress();
        when(progressRepository.findById(100L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> progressService.updateProgress(100L, updated));

        assertTrue(ex.getMessage().contains("Progress non trouvé"));
        verify(progressRepository).findById(100L);
    }

    @Test
    void testDeleteProgress() {
        doNothing().when(progressRepository).deleteById(22L);

        progressService.deleteProgress(22L);

        verify(progressRepository).deleteById(22L);
    }
}
