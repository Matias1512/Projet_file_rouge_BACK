package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.*;
import com.learncode.schoolDev.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProgressServiceTest {

    @Mock
    private ProgressRepository progressRepository;
    
    @Mock
    private SubmissionRepository submissionRepository;
    
    @Mock
    private UserExerciseRepository userExerciseRepository;
    
    @Mock
    private ExerciseRepository exerciseRepository;
    
    @Mock
    private LessonRepository lessonRepository;
    
    @Mock
    private CourseRepository courseRepository;
    
    @Mock
    private UserRepository userRepository;

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

    @Test
    void calculateProgressPercentage_NoExercises_ReturnsZero() {
        when(exerciseRepository.countByCourseId(1L)).thenReturn(0L);

        double result = progressService.calculateProgressPercentage(1L, 1L);

        assertEquals(0.0, result);
        verify(exerciseRepository).countByCourseId(1L);
        verify(userExerciseRepository, never()).countCompletedExercisesByUserAndCourse(anyLong(), anyLong());
    }

    @Test
    void calculateProgressPercentage_WithExercises_CalculatesCorrectly() {
        when(exerciseRepository.countByCourseId(1L)).thenReturn(10L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(1L, 1L)).thenReturn(7L);

        double result = progressService.calculateProgressPercentage(1L, 1L);

        assertEquals(70.0, result);
        verify(exerciseRepository).countByCourseId(1L);
        verify(userExerciseRepository).countCompletedExercisesByUserAndCourse(1L, 1L);
    }

    @Test
    void calculateProgressPercentage_AllCompleted_Returns100() {
        when(exerciseRepository.countByCourseId(1L)).thenReturn(5L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(1L, 1L)).thenReturn(5L);

        double result = progressService.calculateProgressPercentage(1L, 1L);

        assertEquals(100.0, result);
        verify(exerciseRepository).countByCourseId(1L);
        verify(userExerciseRepository).countCompletedExercisesByUserAndCourse(1L, 1L);
    }

    @Test
    void getCurrentLessonId_NoLessons_ReturnsNull() {
        when(lessonRepository.findByCourse_CourseIdOrderByOrderInCourse(1L))
            .thenReturn(Collections.emptyList());

        Long result = progressService.getCurrentLessonId(1L, 1L);

        assertNull(result);
        verify(lessonRepository).findByCourse_CourseIdOrderByOrderInCourse(1L);
    }

    @Test
    void getCurrentLessonId_FirstLessonIncomplete_ReturnsFirstLesson() {
        Lesson lesson1 = new Lesson();
        lesson1.setLessonId(10L);
        Lesson lesson2 = new Lesson();
        lesson2.setLessonId(20L);
        
        Exercise exercise1 = new Exercise();
        exercise1.setExerciseId(100L);
        
        when(lessonRepository.findByCourse_CourseIdOrderByOrderInCourse(1L))
            .thenReturn(Arrays.asList(lesson1, lesson2));
        when(exerciseRepository.findByLesson_LessonId(10L))
            .thenReturn(Arrays.asList(exercise1));
        when(userExerciseRepository.findSuccessfulUserExercisesByUserAndExercise(1L, 100L))
            .thenReturn(Collections.emptyList());

        Long result = progressService.getCurrentLessonId(1L, 1L);

        assertEquals(10L, result);
        verify(lessonRepository).findByCourse_CourseIdOrderByOrderInCourse(1L);
        verify(exerciseRepository).findByLesson_LessonId(10L);
        verify(userExerciseRepository).findSuccessfulUserExercisesByUserAndExercise(1L, 100L);
    }

    @Test
    void getCurrentLessonId_AllCompleted_ReturnsLastLesson() {
        Lesson lesson1 = new Lesson();
        lesson1.setLessonId(10L);
        Lesson lesson2 = new Lesson();
        lesson2.setLessonId(20L);
        
        Exercise exercise1 = new Exercise();
        exercise1.setExerciseId(100L);
        Exercise exercise2 = new Exercise();
        exercise2.setExerciseId(200L);
        
        UserExercise userExercise = new UserExercise();
        
        when(lessonRepository.findByCourse_CourseIdOrderByOrderInCourse(1L))
            .thenReturn(Arrays.asList(lesson1, lesson2));
        when(exerciseRepository.findByLesson_LessonId(10L))
            .thenReturn(Arrays.asList(exercise1));
        when(exerciseRepository.findByLesson_LessonId(20L))
            .thenReturn(Arrays.asList(exercise2));
        when(userExerciseRepository.findSuccessfulUserExercisesByUserAndExercise(1L, 100L))
            .thenReturn(Arrays.asList(userExercise));
        when(userExerciseRepository.findSuccessfulUserExercisesByUserAndExercise(1L, 200L))
            .thenReturn(Arrays.asList(userExercise));

        Long result = progressService.getCurrentLessonId(1L, 1L);

        assertEquals(20L, result);
        verify(lessonRepository).findByCourse_CourseIdOrderByOrderInCourse(1L);
        verify(exerciseRepository).findByLesson_LessonId(10L);
        verify(exerciseRepository).findByLesson_LessonId(20L);
    }

    @Test
    void getCurrentLessonId_SecondLessonIncomplete_ReturnsSecondLesson() {
        Lesson lesson1 = new Lesson();
        lesson1.setLessonId(10L);
        Lesson lesson2 = new Lesson();
        lesson2.setLessonId(20L);
        
        Exercise exercise1 = new Exercise();
        exercise1.setExerciseId(100L);
        Exercise exercise2 = new Exercise();
        exercise2.setExerciseId(200L);
        
        UserExercise userExercise = new UserExercise();
        
        when(lessonRepository.findByCourse_CourseIdOrderByOrderInCourse(1L))
            .thenReturn(Arrays.asList(lesson1, lesson2));
        when(exerciseRepository.findByLesson_LessonId(10L))
            .thenReturn(Arrays.asList(exercise1));
        when(exerciseRepository.findByLesson_LessonId(20L))
            .thenReturn(Arrays.asList(exercise2));
        when(userExerciseRepository.findSuccessfulUserExercisesByUserAndExercise(1L, 100L))
            .thenReturn(Arrays.asList(userExercise));
        when(userExerciseRepository.findSuccessfulUserExercisesByUserAndExercise(1L, 200L))
            .thenReturn(Collections.emptyList());

        Long result = progressService.getCurrentLessonId(1L, 1L);

        assertEquals(20L, result);
        verify(lessonRepository).findByCourse_CourseIdOrderByOrderInCourse(1L);
        verify(exerciseRepository).findByLesson_LessonId(10L);
        verify(exerciseRepository).findByLesson_LessonId(20L);
    }

    @Test
    void updateProgressAutomatically_UserNotFound_Throws() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            progressService.updateProgressAutomatically(999L, 1L);
        });

        assertTrue(ex.getMessage().contains("Utilisateur non trouvé"));
        verify(userRepository).findById(999L);
    }

    @Test
    void updateProgressAutomatically_CourseNotFound_Throws() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            progressService.updateProgressAutomatically(1L, 999L);
        });

        assertTrue(ex.getMessage().contains("Cours non trouvé"));
        verify(userRepository).findById(1L);
        verify(courseRepository).findById(999L);
    }

    @Test
    void updateProgressAutomatically_ExistingProgress_Updates() {
        User user = new User();
        Course course = new Course();
        Progress existingProgress = new Progress();
        existingProgress.setPercentageCompleted(50.0);
        existingProgress.setCurrentLessonId(1L);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(progressRepository.findByUser_UserIdAndCourse_CourseId(1L, 1L))
            .thenReturn(Optional.of(existingProgress));
        when(exerciseRepository.countByCourseId(1L)).thenReturn(10L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(1L, 1L)).thenReturn(7L);
        
        Lesson lesson = new Lesson();
        lesson.setLessonId(2L);
        when(lessonRepository.findByCourse_CourseIdOrderByOrderInCourse(1L))
            .thenReturn(Arrays.asList(lesson));
        when(exerciseRepository.findByLesson_LessonId(2L)).thenReturn(Collections.emptyList());
        
        when(progressRepository.save(existingProgress)).thenReturn(existingProgress);

        Progress result = progressService.updateProgressAutomatically(1L, 1L);

        assertEquals(70.0, result.getPercentageCompleted());
        assertEquals(2L, result.getCurrentLessonId());
        verify(progressRepository).save(existingProgress);
    }

    @Test
    void updateProgressAutomatically_NewProgress_Creates() {
        User user = new User();
        Course course = new Course();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(progressRepository.findByUser_UserIdAndCourse_CourseId(1L, 1L))
            .thenReturn(Optional.empty());
        when(exerciseRepository.countByCourseId(1L)).thenReturn(5L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(1L, 1L)).thenReturn(2L);
        
        Lesson lesson = new Lesson();
        lesson.setLessonId(3L);
        when(lessonRepository.findByCourse_CourseIdOrderByOrderInCourse(1L))
            .thenReturn(Arrays.asList(lesson));
        when(exerciseRepository.findByLesson_LessonId(3L)).thenReturn(Collections.emptyList());
        
        Progress newProgress = new Progress();
        when(progressRepository.save(any(Progress.class))).thenReturn(newProgress);

        Progress result = progressService.updateProgressAutomatically(1L, 1L);

        assertNotNull(result);
        verify(progressRepository).save(any(Progress.class));
    }

    @Test
    void updateProgressAfterSubmission_ExerciseNotFound_Throws() {
        when(exerciseRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            progressService.updateProgressAfterSubmission(1L, 999L);
        });

        assertTrue(ex.getMessage().contains("Exercice non trouvé"));
        verify(exerciseRepository).findById(999L);
    }

    @Test
    void updateProgressAfterSubmission_Success() {
        Exercise exercise = new Exercise();
        Lesson lesson = new Lesson();
        Course course = new Course();
        course.setCourseId(1L);
        lesson.setCourse(course);
        exercise.setLesson(lesson);
        
        User user = new User();
        Progress progress = new Progress();
        
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(progressRepository.findByUser_UserIdAndCourse_CourseId(1L, 1L))
            .thenReturn(Optional.of(progress));
        when(exerciseRepository.countByCourseId(1L)).thenReturn(1L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(1L, 1L)).thenReturn(0L);
        when(lessonRepository.findByCourse_CourseIdOrderByOrderInCourse(1L))
            .thenReturn(Collections.emptyList());
        when(progressRepository.save(progress)).thenReturn(progress);

        progressService.updateProgressAfterSubmission(1L, 1L);

        verify(exerciseRepository).findById(1L);
        verify(progressRepository).save(progress);
    }

    @Test
    void initializeProgressForCourse_ExistingProgress_ReturnsExisting() {
        Progress existingProgress = new Progress();
        when(progressRepository.findByUser_UserIdAndCourse_CourseId(1L, 1L))
            .thenReturn(Optional.of(existingProgress));

        Progress result = progressService.initializeProgressForCourse(1L, 1L);

        assertEquals(existingProgress, result);
        verify(progressRepository).findByUser_UserIdAndCourse_CourseId(1L, 1L);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void initializeProgressForCourse_NoExistingProgress_CreatesNew() {
        User user = new User();
        Course course = new Course();
        Progress newProgress = new Progress();
        
        when(progressRepository.findByUser_UserIdAndCourse_CourseId(1L, 1L))
            .thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(exerciseRepository.countByCourseId(1L)).thenReturn(0L);
        when(lessonRepository.findByCourse_CourseIdOrderByOrderInCourse(1L))
            .thenReturn(Collections.emptyList());
        when(progressRepository.save(any(Progress.class))).thenReturn(newProgress);

        Progress result = progressService.initializeProgressForCourse(1L, 1L);

        assertEquals(newProgress, result);
        verify(progressRepository, times(2)).findByUser_UserIdAndCourse_CourseId(1L, 1L);
        verify(progressRepository).save(any(Progress.class));
    }

    @Test
    void isCourseCompleted_NotCompleted_ReturnsFalse() {
        when(exerciseRepository.countByCourseId(1L)).thenReturn(10L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(1L, 1L)).thenReturn(7L);

        boolean result = progressService.isCourseCompleted(1L, 1L);

        assertFalse(result);
        verify(exerciseRepository).countByCourseId(1L);
        verify(userExerciseRepository).countCompletedExercisesByUserAndCourse(1L, 1L);
    }

    @Test
    void isCourseCompleted_Completed_ReturnsTrue() {
        when(exerciseRepository.countByCourseId(1L)).thenReturn(5L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(1L, 1L)).thenReturn(5L);

        boolean result = progressService.isCourseCompleted(1L, 1L);

        assertTrue(result);
        verify(exerciseRepository).countByCourseId(1L);
        verify(userExerciseRepository).countCompletedExercisesByUserAndCourse(1L, 1L);
    }

    @Test
    void isCourseCompleted_OverCompleted_ReturnsTrue() {
        when(exerciseRepository.countByCourseId(1L)).thenReturn(5L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(1L, 1L)).thenReturn(6L);

        boolean result = progressService.isCourseCompleted(1L, 1L);

        assertTrue(result);
        verify(exerciseRepository).countByCourseId(1L);
        verify(userExerciseRepository).countCompletedExercisesByUserAndCourse(1L, 1L);
    }
}
