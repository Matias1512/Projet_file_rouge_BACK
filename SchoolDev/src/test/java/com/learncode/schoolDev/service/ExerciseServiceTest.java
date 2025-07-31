package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.Exercise;
import com.learncode.schoolDev.model.ExerciseType;
import com.learncode.schoolDev.repository.ExerciseRepository;
import com.learncode.schoolDev.repository.QcmPropositionRepository;
import com.learncode.schoolDev.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;
    
    @Mock
    private QcmPropositionRepository qcmPropositionRepository;
    
    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllExercises_ReturnsList() {
        List<Exercise> exercises = Arrays.asList(new Exercise(), new Exercise());
        when(exerciseRepository.findAll()).thenReturn(exercises);

        List<Exercise> result = exerciseService.getAllExercises();

        assertEquals(2, result.size());
        verify(exerciseRepository).findAll();
    }

    @Test
    void getExerciseById_Found() {
        Exercise exercise = new Exercise();
        exercise.setExerciseId(1L);

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        Optional<Exercise> result = exerciseService.getExerciseById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getExerciseId());
        verify(exerciseRepository).findById(1L);
    }

    @Test
    void getExerciseById_NotFound() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Exercise> result = exerciseService.getExerciseById(1L);

        assertFalse(result.isPresent());
        verify(exerciseRepository).findById(1L);
    }

    @Test
    void getExercisesByLesson_ReturnsList() {
        List<Exercise> exercises = Arrays.asList(new Exercise(), new Exercise());
        when(exerciseRepository.findByLesson_LessonId(5L)).thenReturn(exercises);

        List<Exercise> result = exerciseService.getExercisesByLesson(5L);

        assertEquals(2, result.size());
        verify(exerciseRepository).findByLesson_LessonId(5L);
    }

    @Test
    void createExercise_ReturnsSaved() {
        Exercise exercise = new Exercise();
        exercise.setTitle("Test Title");
        exercise.setDescription("Test Description");
        exercise.setType(ExerciseType.CODE);
        exercise.setStarterCode("int x = 0;");
        exercise.setTestCases("assert x == 0");
        when(exerciseRepository.save(exercise)).thenReturn(exercise);

        Exercise result = exerciseService.createExercise(exercise);

        assertEquals("Test Title", result.getTitle());
        verify(exerciseRepository).save(exercise);
    }

    @Test
    void updateExercise_Success() {
        Exercise existing = new Exercise();
        existing.setExerciseId(1L);
        existing.setTitle("Old Title");

        Exercise updated = new Exercise();
        updated.setTitle("New Title");
        updated.setDescription("New Desc");
        updated.setStarterCode("new code");
        updated.setTestCases("new tests");

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Exercise result = exerciseService.updateExercise(1L, updated);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Desc", result.getDescription());
        assertEquals("new code", result.getStarterCode());
        assertEquals("new tests", result.getTestCases());
        verify(exerciseRepository).findById(1L);
        verify(exerciseRepository).save(existing);
    }

    @Test
    void updateExercise_NotFound_Throws() {
        Exercise updated = new Exercise();
        when(exerciseRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            exerciseService.updateExercise(99L, updated);
        });

        assertTrue(ex.getMessage().contains("Exercice non trouvé"));
        verify(exerciseRepository).findById(99L);
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void deleteExercise_CallsRepository() {
        when(qcmPropositionRepository.findByExercise_ExerciseId(7L)).thenReturn(Collections.emptyList());
        doNothing().when(qcmPropositionRepository).deleteAll(any());
        doNothing().when(exerciseRepository).deleteById(7L);

        exerciseService.deleteExercise(7L);

        verify(qcmPropositionRepository).findByExercise_ExerciseId(7L);
        verify(qcmPropositionRepository).deleteAll(any());
        verify(exerciseRepository).deleteById(7L);
    }
}
