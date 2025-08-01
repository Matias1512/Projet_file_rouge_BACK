package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.dto.ExerciseResponse;
import com.learncode.schoolDev.dto.ExerciseListResponse;
import com.learncode.schoolDev.model.Exercise;
import com.learncode.schoolDev.service.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseControllerTest {

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private ExerciseController exerciseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllExercises() {
        Exercise ex1 = createExercise(1L, "Ex 1");
        Exercise ex2 = createExercise(2L, "Ex 2");
        when(exerciseService.getAllExercises()).thenReturn(Arrays.asList(ex1, ex2));

        List<Exercise> result = exerciseController.getAllExercises();

        assertEquals(2, result.size());
        assertEquals("Ex 1", result.get(0).getTitle());
        assertEquals("Ex 2", result.get(1).getTitle());
        verify(exerciseService).getAllExercises();
    }

    @Test
    void testGetExerciseById_Found() {
        ExerciseResponse exDto = createExerciseResponse(10L, "Test");
        when(exerciseService.getExerciseByIdAsDto(10L)).thenReturn(Optional.of(exDto));

        ResponseEntity<ExerciseResponse> response = exerciseController.getExerciseById(10L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(exDto, response.getBody());
    }

    @Test
    void testGetExerciseById_NotFound() {
        when(exerciseService.getExerciseByIdAsDto(99L)).thenReturn(Optional.empty());

        ResponseEntity<ExerciseResponse> response = exerciseController.getExerciseById(99L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testGetExercisesByLesson() {
        ExerciseListResponse exDto = createExerciseListResponse(3L, "LessonEx");
        when(exerciseService.getExercisesByLessonAsDto(5L)).thenReturn(List.of(exDto));

        List<ExerciseListResponse> result = exerciseController.getExercisesByLesson(5L);

        assertEquals(1, result.size());
        assertEquals("LessonEx", result.get(0).getTitle());
        verify(exerciseService).getExercisesByLessonAsDto(5L);
    }

    @Test
    void testCreateExercise() {
        Exercise input = createExercise(null, "ToCreate");
        Exercise saved = createExercise(12L, "ToCreate");
        when(exerciseService.createExercise(input)).thenReturn(saved);

        ResponseEntity<?> response = exerciseController.createExercise(input);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(saved, response.getBody());
        verify(exerciseService).createExercise(input);
    }

    @Test
    void testUpdateExercise_Success() {
        Exercise updated = createExercise(4L, "Updated");
        when(exerciseService.updateExercise(4L, updated)).thenReturn(updated);

        ResponseEntity<Exercise> response = exerciseController.updateExercise(4L, updated);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updated, response.getBody());
    }

    @Test
    void testUpdateExercise_NotFound() {
        Exercise ex = createExercise(66L, "Fail");
        when(exerciseService.updateExercise(eq(66L), any())).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<Exercise> response = exerciseController.updateExercise(66L, ex);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteExercise() {
        doNothing().when(exerciseService).deleteExercise(8L);

        ResponseEntity<Void> response = exerciseController.deleteExercise(8L);

        assertEquals(204, response.getStatusCode().value());
        verify(exerciseService).deleteExercise(8L);
    }

    // Méthode utilitaire pour créer des exercices
    private Exercise createExercise(Long id, String title) {
        Exercise ex = new Exercise();
        ex.setExerciseId(id);
        ex.setTitle(title);
        ex.setDescription("desc");
        ex.setStarterCode("code");
        ex.setTestCases("cases");
        return ex;
    }
    
    // Méthode utilitaire pour créer des ExerciseResponse
    private ExerciseResponse createExerciseResponse(Long id, String title) {
        ExerciseResponse dto = new ExerciseResponse();
        dto.setExerciseId(id);
        dto.setTitle(title);
        dto.setDescription("desc");
        return dto;
    }
    
    // Méthode utilitaire pour créer des ExerciseListResponse
    private ExerciseListResponse createExerciseListResponse(Long id, String title) {
        ExerciseListResponse dto = new ExerciseListResponse();
        dto.setExerciseId(id);
        dto.setTitle(title);
        dto.setDescription("desc");
        return dto;
    }
}
