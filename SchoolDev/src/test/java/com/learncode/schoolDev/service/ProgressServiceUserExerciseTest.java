package com.learncode.schoolDev.service;

import com.learncode.schoolDev.repository.ProgressRepository;
import com.learncode.schoolDev.repository.UserExerciseRepository;
import com.learncode.schoolDev.repository.ExerciseRepository;
import com.learncode.schoolDev.repository.LessonRepository;
import com.learncode.schoolDev.repository.CourseRepository;
import com.learncode.schoolDev.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProgressServiceUserExerciseTest {

    @Mock
    private ProgressRepository progressRepository;
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
    void testCalculateProgressPercentage_WithUserExercise() {
        Long userId = 3L;
        Long courseId = 1L;
        
        when(exerciseRepository.countByCourseId(courseId)).thenReturn(10L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(userId, courseId)).thenReturn(5L);

        double result = progressService.calculateProgressPercentage(userId, courseId);

        assertEquals(50.0, result);
        verify(exerciseRepository).countByCourseId(courseId);
        verify(userExerciseRepository).countCompletedExercisesByUserAndCourse(userId, courseId);
    }

    @Test
    void testCalculateProgressPercentage_NoCourse() {
        Long userId = 3L;
        Long courseId = 999L;
        
        when(exerciseRepository.countByCourseId(courseId)).thenReturn(0L);

        double result = progressService.calculateProgressPercentage(userId, courseId);

        assertEquals(0.0, result);
        verify(exerciseRepository).countByCourseId(courseId);
        verifyNoInteractions(userExerciseRepository);
    }

    @Test
    void testCalculateProgressPercentage_FullComplete() {
        Long userId = 3L;
        Long courseId = 1L;
        
        when(exerciseRepository.countByCourseId(courseId)).thenReturn(8L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(userId, courseId)).thenReturn(8L);

        double result = progressService.calculateProgressPercentage(userId, courseId);

        assertEquals(100.0, result);
    }

    @Test
    void testIsCourseCompleted_True() {
        Long userId = 3L;
        Long courseId = 1L;
        
        when(exerciseRepository.countByCourseId(courseId)).thenReturn(5L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(userId, courseId)).thenReturn(5L);

        boolean result = progressService.isCourseCompleted(userId, courseId);

        assertTrue(result);
    }

    @Test
    void testIsCourseCompleted_False() {
        Long userId = 3L;
        Long courseId = 1L;
        
        when(exerciseRepository.countByCourseId(courseId)).thenReturn(10L);
        when(userExerciseRepository.countCompletedExercisesByUserAndCourse(userId, courseId)).thenReturn(7L);

        boolean result = progressService.isCourseCompleted(userId, courseId);

        assertFalse(result);
    }
}