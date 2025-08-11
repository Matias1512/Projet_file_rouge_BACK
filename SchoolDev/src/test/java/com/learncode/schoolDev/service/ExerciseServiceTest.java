package com.learncode.schoolDev.service;

import com.learncode.schoolDev.dto.QcmPropositionRequest;
import com.learncode.schoolDev.dto.ExerciseResponse;
import com.learncode.schoolDev.dto.ExerciseListResponse;
import com.learncode.schoolDev.dto.QcmPropositionResponse;
import com.learncode.schoolDev.model.Exercise;
import com.learncode.schoolDev.model.ExerciseType;
import com.learncode.schoolDev.model.QcmProposition;
import com.learncode.schoolDev.model.Lesson;
import com.learncode.schoolDev.repository.ExerciseRepository;
import com.learncode.schoolDev.repository.QcmPropositionRepository;
import com.learncode.schoolDev.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
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

    @Test
    void createExercise_CodeExercise_ValidatesStarterCode() {
        Exercise exercise = new Exercise();
        exercise.setType(ExerciseType.CODE);
        exercise.setStarterCode("");
        exercise.setTestCases("test");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            exerciseService.createExercise(exercise);
        });

        assertTrue(ex.getMessage().contains("code de démarrage est obligatoire"));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void createExercise_CodeExercise_ValidatesTestCases() {
        Exercise exercise = new Exercise();
        exercise.setType(ExerciseType.CODE);
        exercise.setStarterCode("code");
        exercise.setTestCases("");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            exerciseService.createExercise(exercise);
        });

        assertTrue(ex.getMessage().contains("cas de test sont obligatoires"));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void createExercise_CodeExercise_RejectsQcmPropositions() {
        Exercise exercise = new Exercise();
        exercise.setType(ExerciseType.CODE);
        exercise.setStarterCode("code");
        exercise.setTestCases("test");
        
        List<QcmPropositionRequest> propositions = Arrays.asList(new QcmPropositionRequest());
        exercise.setPropositionsForApi(propositions);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            exerciseService.createExercise(exercise);
        });

        assertTrue(ex.getMessage().contains("ne doit pas avoir de propositions QCM"));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void createExercise_QcmExercise_ValidatesPropositionCount() {
        Exercise exercise = new Exercise();
        exercise.setType(ExerciseType.QCM);
        
        List<QcmPropositionRequest> propositions = Arrays.asList(
            new QcmPropositionRequest(), 
            new QcmPropositionRequest()
        );
        exercise.setPropositionsForApi(propositions);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            exerciseService.createExercise(exercise);
        });

        assertTrue(ex.getMessage().contains("exactement 3 propositions"));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void createExercise_QcmExercise_ValidatesCorrectAnswer() {
        Exercise exercise = new Exercise();
        exercise.setType(ExerciseType.QCM);
        
        QcmPropositionRequest prop1 = new QcmPropositionRequest();
        prop1.setIsCorrect(false);
        QcmPropositionRequest prop2 = new QcmPropositionRequest();
        prop2.setIsCorrect(false);
        QcmPropositionRequest prop3 = new QcmPropositionRequest();
        prop3.setIsCorrect(false);
        
        List<QcmPropositionRequest> propositions = Arrays.asList(prop1, prop2, prop3);
        exercise.setPropositionsForApi(propositions);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            exerciseService.createExercise(exercise);
        });

        assertTrue(ex.getMessage().contains("au moins une bonne réponse"));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void createExercise_QcmExercise_RejectsStarterCode() {
        Exercise exercise = new Exercise();
        exercise.setType(ExerciseType.QCM);
        exercise.setStarterCode("code");
        
        QcmPropositionRequest prop = new QcmPropositionRequest();
        prop.setIsCorrect(true);
        List<QcmPropositionRequest> propositions = Arrays.asList(prop, new QcmPropositionRequest(), new QcmPropositionRequest());
        exercise.setPropositionsForApi(propositions);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            exerciseService.createExercise(exercise);
        });

        assertTrue(ex.getMessage().contains("ne doit pas avoir de code de démarrage"));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void createExercise_QcmExercise_RejectsTestCases() {
        Exercise exercise = new Exercise();
        exercise.setType(ExerciseType.QCM);
        exercise.setTestCases("tests");
        
        QcmPropositionRequest prop = new QcmPropositionRequest();
        prop.setIsCorrect(true);
        List<QcmPropositionRequest> propositions = Arrays.asList(prop, new QcmPropositionRequest(), new QcmPropositionRequest());
        exercise.setPropositionsForApi(propositions);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            exerciseService.createExercise(exercise);
        });

        assertTrue(ex.getMessage().contains("ne doit pas avoir de cas de test"));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void createExercise_QcmExercise_Success() {
        Exercise exercise = new Exercise();
        exercise.setType(ExerciseType.QCM);
        exercise.setTitle("QCM Test");
        
        QcmPropositionRequest prop1 = new QcmPropositionRequest();
        prop1.setText("Option 1");
        prop1.setIsCorrect(true);
        QcmPropositionRequest prop2 = new QcmPropositionRequest();
        prop2.setText("Option 2");
        prop2.setIsCorrect(false);
        QcmPropositionRequest prop3 = new QcmPropositionRequest();
        prop3.setText("Option 3");
        prop3.setIsCorrect(false);
        
        List<QcmPropositionRequest> propositions = Arrays.asList(prop1, prop2, prop3);
        exercise.setPropositionsForApi(propositions);
        
        Exercise savedExercise = new Exercise();
        savedExercise.setType(ExerciseType.QCM);
        savedExercise.setExerciseId(1L);
        
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(savedExercise);

        Exercise result = exerciseService.createExercise(exercise);

        assertNotNull(result);
        assertEquals(ExerciseType.QCM, result.getType());
        verify(exerciseRepository).save(exercise);
        verify(qcmPropositionRepository, times(3)).save(any(QcmProposition.class));
    }

    @Test
    void createExercise_WithLesson_Success() {
        Exercise exercise = new Exercise();
        exercise.setType(ExerciseType.CODE);
        exercise.setStarterCode("code");
        exercise.setTestCases("test");
        exercise.setLessonIdForApi(1L);
        
        Lesson lesson = new Lesson();
        lesson.setLessonId(1L);
        
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        Exercise result = exerciseService.createExercise(exercise);

        assertNotNull(result);
        verify(lessonRepository).findById(1L);
        verify(exerciseRepository).save(exercise);
        assertEquals(lesson, exercise.getLesson());
    }

    @Test
    void createExercise_LessonNotFound_Throws() {
        Exercise exercise = new Exercise();
        exercise.setType(ExerciseType.CODE);
        exercise.setStarterCode("code");
        exercise.setTestCases("test");
        exercise.setLessonIdForApi(999L);
        
        when(lessonRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            exerciseService.createExercise(exercise);
        });

        assertTrue(ex.getMessage().contains("Leçon non trouvée"));
        verify(lessonRepository).findById(999L);
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void getQcmPropositionsByExercise_ReturnsList() {
        List<QcmProposition> propositions = Arrays.asList(new QcmProposition(), new QcmProposition());
        when(qcmPropositionRepository.findByExercise_ExerciseId(1L)).thenReturn(propositions);

        List<QcmProposition> result = exerciseService.getQcmPropositionsByExercise(1L);

        assertEquals(2, result.size());
        verify(qcmPropositionRepository).findByExercise_ExerciseId(1L);
    }

    @Test
    void getCorrectPropositionsByExercise_ReturnsList() {
        List<QcmProposition> correctPropositions = Arrays.asList(new QcmProposition());
        when(qcmPropositionRepository.findCorrectPropositionsByExerciseId(1L)).thenReturn(correctPropositions);

        List<QcmProposition> result = exerciseService.getCorrectPropositionsByExercise(1L);

        assertEquals(1, result.size());
        verify(qcmPropositionRepository).findCorrectPropositionsByExerciseId(1L);
    }

    @Test
    void getExerciseByIdAsDto_Found() {
        Exercise exercise = new Exercise();
        exercise.setExerciseId(1L);
        exercise.setTitle("Test");
        exercise.setDescription("Description");
        exercise.setType(ExerciseType.CODE);
        exercise.setStarterCode("code");
        exercise.setTestCases("test");
        
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        Optional<ExerciseResponse> result = exerciseService.getExerciseByIdAsDto(1L);

        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getTitle());
        assertEquals(ExerciseType.CODE, result.get().getType());
        verify(exerciseRepository).findById(1L);
    }

    @Test
    void getExerciseByIdAsDto_NotFound() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ExerciseResponse> result = exerciseService.getExerciseByIdAsDto(1L);

        assertFalse(result.isPresent());
        verify(exerciseRepository).findById(1L);
    }

    @Test
    void getExerciseByIdAsDto_QcmWithPropositions() {
        Exercise exercise = new Exercise();
        exercise.setExerciseId(1L);
        exercise.setTitle("QCM Test");
        exercise.setType(ExerciseType.QCM);
        
        Lesson lesson = new Lesson();
        lesson.setLessonId(2L);
        exercise.setLesson(lesson);
        
        QcmProposition prop1 = new QcmProposition();
        prop1.setPropositionId(1L);
        prop1.setText("Option 1");
        prop1.setCorrect(true);
        
        QcmProposition prop2 = new QcmProposition();
        prop2.setPropositionId(2L);
        prop2.setText("Option 2");
        prop2.setCorrect(false);
        
        List<QcmProposition> propositions = Arrays.asList(prop1, prop2);
        
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(qcmPropositionRepository.findByExercise_ExerciseId(1L)).thenReturn(propositions);

        Optional<ExerciseResponse> result = exerciseService.getExerciseByIdAsDto(1L);

        assertTrue(result.isPresent());
        ExerciseResponse dto = result.get();
        assertEquals("QCM Test", dto.getTitle());
        assertEquals(ExerciseType.QCM, dto.getType());
        assertEquals(2L, dto.getLessonId());
        assertNotNull(dto.getPropositions());
        assertEquals(2, dto.getPropositions().size());
        assertEquals("Option 1", dto.getPropositions().get(0).getText());
        assertTrue(dto.getPropositions().get(0).isCorrect());
        verify(exerciseRepository).findById(1L);
        verify(qcmPropositionRepository).findByExercise_ExerciseId(1L);
    }

    @Test
    void getExercisesByLessonAsDto_ReturnsList() {
        Exercise exercise1 = new Exercise();
        exercise1.setExerciseId(1L);
        exercise1.setTitle("Exercise 1");
        exercise1.setDescription("Desc 1");
        exercise1.setType(ExerciseType.CODE);
        
        Exercise exercise2 = new Exercise();
        exercise2.setExerciseId(2L);
        exercise2.setTitle("Exercise 2");
        exercise2.setDescription("Desc 2");
        exercise2.setType(ExerciseType.QCM);
        
        List<Exercise> exercises = Arrays.asList(exercise1, exercise2);
        when(exerciseRepository.findByLesson_LessonId(3L)).thenReturn(exercises);

        List<ExerciseListResponse> result = exerciseService.getExercisesByLessonAsDto(3L);

        assertEquals(2, result.size());
        assertEquals("Exercise 1", result.get(0).getTitle());
        assertEquals(ExerciseType.CODE, result.get(0).getType());
        assertEquals("Exercise 2", result.get(1).getTitle());
        assertEquals(ExerciseType.QCM, result.get(1).getType());
        verify(exerciseRepository).findByLesson_LessonId(3L);
    }

    @Test
    void updateExercise_UpdatesType() {
        Exercise existing = new Exercise();
        existing.setExerciseId(1L);
        existing.setType(ExerciseType.CODE);

        Exercise updated = new Exercise();
        updated.setTitle("New Title");
        updated.setDescription("New Desc");
        updated.setType(ExerciseType.QCM);
        updated.setStarterCode("new code");
        updated.setTestCases("new tests");

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Exercise result = exerciseService.updateExercise(1L, updated);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Desc", result.getDescription());
        assertEquals(ExerciseType.QCM, result.getType());
        assertEquals("new code", result.getStarterCode());
        assertEquals("new tests", result.getTestCases());
        verify(exerciseRepository).findById(1L);
        verify(exerciseRepository).save(existing);
    }
}
