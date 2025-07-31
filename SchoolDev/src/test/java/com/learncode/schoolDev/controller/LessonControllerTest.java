package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.dto.LessonCreateRequest;
import com.learncode.schoolDev.model.Lesson;
import com.learncode.schoolDev.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonControllerTest {

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController lessonController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllLessons() {
        Lesson l1 = createLesson(1L, "L1");
        Lesson l2 = createLesson(2L, "L2");
        when(lessonService.getAllLessons()).thenReturn(Arrays.asList(l1, l2));

        List<Lesson> result = lessonController.getAllLessons();

        assertEquals(2, result.size());
        assertEquals("L1", result.get(0).getTitle());
        assertEquals("L2", result.get(1).getTitle());
        verify(lessonService).getAllLessons();
    }

    @Test
    void testGetLessonById_Found() {
        Lesson l = createLesson(5L, "Found");
        when(lessonService.getLessonById(5L)).thenReturn(Optional.of(l));

        ResponseEntity<Lesson> response = lessonController.getLessonById(5L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(l, response.getBody());
    }

    @Test
    void testGetLessonById_NotFound() {
        when(lessonService.getLessonById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Lesson> response = lessonController.getLessonById(99L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testGetLessonsByCourse() {
        Lesson l = createLesson(3L, "CourseL");
        when(lessonService.getLessonsByCourse(7L)).thenReturn(List.of(l));

        List<Lesson> result = lessonController.getLessonsByCourse(7L);

        assertEquals(1, result.size());
        assertEquals("CourseL", result.get(0).getTitle());
        verify(lessonService).getLessonsByCourse(7L);
    }

    @Test
    void testCreateLesson() {
        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("ToCreate");
        request.setContent("Content");
        request.setOrderInCourse(1);
        request.setCourseId(1L);
        
        Lesson saved = createLesson(12L, "ToCreate");
        when(lessonService.createLessonFromRequest(request)).thenReturn(saved);

        ResponseEntity<Lesson> response = lessonController.createLesson(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(saved, response.getBody());
        verify(lessonService).createLessonFromRequest(request);
    }

    @Test
    void testCreateLesson_BadRequest() {
        LessonCreateRequest request = new LessonCreateRequest();
        request.setTitle("ToCreate");
        request.setContent("Content");
        request.setOrderInCourse(1);
        request.setCourseId(999L);
        
        when(lessonService.createLessonFromRequest(request)).thenThrow(new RuntimeException("Course not found"));

        ResponseEntity<Lesson> response = lessonController.createLesson(request);

        assertEquals(400, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(lessonService).createLessonFromRequest(request);
    }

    @Test
    void testUpdateLesson_Success() {
        Lesson updated = createLesson(8L, "Updated");
        when(lessonService.updateLesson(8L, updated)).thenReturn(updated);

        ResponseEntity<Lesson> response = lessonController.updateLesson(8L, updated);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updated, response.getBody());
    }

    @Test
    void testUpdateLesson_NotFound() {
        Lesson l = createLesson(77L, "NotFound");
        when(lessonService.updateLesson(eq(77L), any())).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<Lesson> response = lessonController.updateLesson(77L, l);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteLesson() {
        doNothing().when(lessonService).deleteLesson(15L);

        ResponseEntity<Void> response = lessonController.deleteLesson(15L);

        assertEquals(204, response.getStatusCode().value());
        verify(lessonService).deleteLesson(15L);
    }

    // Méthode utilitaire pour créer une leçon
    private Lesson createLesson(Long id, String title) {
        Lesson lesson = new Lesson();
        lesson.setLessonId(id);
        lesson.setTitle(title);
        // Ajoute d'autres setters si besoin
        return lesson;
    }
}
