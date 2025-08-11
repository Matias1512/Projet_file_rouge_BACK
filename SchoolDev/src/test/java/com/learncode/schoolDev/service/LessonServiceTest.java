package com.learncode.schoolDev.service;

import com.learncode.schoolDev.dto.LessonCreateRequest;
import com.learncode.schoolDev.model.Course;
import com.learncode.schoolDev.model.Lesson;
import com.learncode.schoolDev.repository.CourseRepository;
import com.learncode.schoolDev.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private LessonService lessonService;

    private Course testCourse;
    private Lesson testLesson;
    private LessonCreateRequest testRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup test data
        testCourse = new Course();
        testCourse.setCourseId(1L);
        testCourse.setTitle("Test Course");
        
        testLesson = new Lesson();
        testLesson.setLessonId(1L);
        testLesson.setTitle("Test Lesson");
        testLesson.setContent("Test Content");
        testLesson.setOrderInCourse(1);
        testLesson.setCourse(testCourse);
        
        testRequest = new LessonCreateRequest();
        testRequest.setTitle("New Lesson Title");
        testRequest.setContent("New Lesson Content");
        testRequest.setOrderInCourse(2);
        testRequest.setCourseId(1L);
    }

    @Test
    void getAllLessons_ReturnsAllLessons() {
        // Given
        List<Lesson> expectedLessons = Arrays.asList(testLesson, new Lesson());
        when(lessonRepository.findAll()).thenReturn(expectedLessons);

        // When
        List<Lesson> result = lessonService.getAllLessons();

        // Then
        assertEquals(2, result.size());
        assertEquals(expectedLessons, result);
        verify(lessonRepository).findAll();
    }

    @Test
    void getAllLessons_ReturnsEmptyList() {
        // Given
        when(lessonRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Lesson> result = lessonService.getAllLessons();

        // Then
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(lessonRepository).findAll();
    }

    @Test
    void getLessonById_Found_ReturnsLesson() {
        // Given
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));

        // When
        Optional<Lesson> result = lessonService.getLessonById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testLesson, result.get());
        assertEquals(1L, result.get().getLessonId());
        assertEquals("Test Lesson", result.get().getTitle());
        verify(lessonRepository).findById(1L);
    }

    @Test
    void getLessonById_NotFound_ReturnsEmpty() {
        // Given
        when(lessonRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Lesson> result = lessonService.getLessonById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(lessonRepository).findById(99L);
    }

    @Test
    void getLessonsByCourse_ReturnsLessonsForCourse() {
        // Given
        List<Lesson> courseLessons = Arrays.asList(testLesson, new Lesson());
        when(lessonRepository.findByCourse_CourseId(1L)).thenReturn(courseLessons);

        // When
        List<Lesson> result = lessonService.getLessonsByCourse(1L);

        // Then
        assertEquals(2, result.size());
        assertEquals(courseLessons, result);
        verify(lessonRepository).findByCourse_CourseId(1L);
    }

    @Test
    void getLessonsByCourse_NoLessonsFound_ReturnsEmptyList() {
        // Given
        when(lessonRepository.findByCourse_CourseId(99L)).thenReturn(Arrays.asList());

        // When
        List<Lesson> result = lessonService.getLessonsByCourse(99L);

        // Then
        assertTrue(result.isEmpty());
        verify(lessonRepository).findByCourse_CourseId(99L);
    }

    @Test
    void createLesson_Success_ReturnsSavedLesson() {
        // Given
        Lesson newLesson = new Lesson();
        newLesson.setTitle("New Lesson");
        newLesson.setContent("New Content");
        when(lessonRepository.save(newLesson)).thenReturn(newLesson);

        // When
        Lesson result = lessonService.createLesson(newLesson);

        // Then
        assertEquals("New Lesson", result.getTitle());
        assertEquals("New Content", result.getContent());
        verify(lessonRepository).save(newLesson);
    }

    @Test
    void createLessonFromRequest_Success_CreatesAndReturnsLesson() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> {
            Lesson savedLesson = invocation.getArgument(0);
            savedLesson.setLessonId(2L);
            return savedLesson;
        });

        // When
        Lesson result = lessonService.createLessonFromRequest(testRequest);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getLessonId());
        assertEquals("New Lesson Title", result.getTitle());
        assertEquals("New Lesson Content", result.getContent());
        assertEquals(2, result.getOrderInCourse());
        assertEquals(testCourse, result.getCourse());
        
        verify(courseRepository).findById(1L);
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    void createLessonFromRequest_CourseNotFound_ThrowsRuntimeException() {
        // Given
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        testRequest.setCourseId(99L);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lessonService.createLessonFromRequest(testRequest);
        });

        assertTrue(exception.getMessage().contains("Cours non trouvé avec ID : 99"));
        verify(courseRepository).findById(99L);
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void updateLesson_Success_UpdatesAllFields() {
        // Given
        Lesson existingLesson = new Lesson();
        existingLesson.setLessonId(1L);
        existingLesson.setTitle("Old Title");
        existingLesson.setContent("Old Content");
        existingLesson.setOrderInCourse(1);
        existingLesson.setCourse(new Course());

        Course newCourse = new Course();
        newCourse.setCourseId(2L);
        
        Lesson updatedLesson = new Lesson();
        updatedLesson.setTitle("New Title");
        updatedLesson.setContent("New Content");
        updatedLesson.setOrderInCourse(3);
        updatedLesson.setCourse(newCourse);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existingLesson));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Lesson result = lessonService.updateLesson(1L, updatedLesson);

        // Then
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Content", result.getContent());
        assertEquals(3, result.getOrderInCourse());
        assertEquals(newCourse, result.getCourse());
        assertEquals(1L, result.getLessonId()); // ID should remain unchanged
        
        verify(lessonRepository).findById(1L);
        verify(lessonRepository).save(existingLesson);
    }

    @Test
    void updateLesson_NotFound_ThrowsRuntimeException() {
        // Given
        Lesson updatedLesson = new Lesson();
        updatedLesson.setTitle("New Title");
        when(lessonRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lessonService.updateLesson(99L, updatedLesson);
        });
        
        assertTrue(exception.getMessage().contains("Leçon non trouvée avec ID : 99"));
        verify(lessonRepository).findById(99L);
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void updateLesson_WithNullAndDefaultFields_UpdatesCorrectly() {
        // Given
        Lesson existingLesson = new Lesson();
        existingLesson.setLessonId(1L);
        existingLesson.setTitle("Old Title");
        existingLesson.setContent("Old Content");
        existingLesson.setOrderInCourse(5);
        existingLesson.setCourse(testCourse);

        Lesson updatedLesson = new Lesson();
        updatedLesson.setTitle("New Title");
        updatedLesson.setContent(null);
        updatedLesson.setOrderInCourse(0); // Default int value
        updatedLesson.setCourse(null);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existingLesson));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Lesson result = lessonService.updateLesson(1L, updatedLesson);

        // Then
        assertEquals("New Title", result.getTitle());
        assertNull(result.getContent()); // Will be set to null
        assertEquals(0, result.getOrderInCourse()); // Will be set to 0 (default int value)
        assertNull(result.getCourse()); // Will be set to null
        
        verify(lessonRepository).findById(1L);
        verify(lessonRepository).save(existingLesson);
    }

    @Test
    void deleteLesson_Success_CallsRepositoryDelete() {
        // Given
        doNothing().when(lessonRepository).deleteById(1L);

        // When
        lessonService.deleteLesson(1L);

        // Then
        verify(lessonRepository).deleteById(1L);
    }

    @Test
    void deleteLesson_WithNullId_CallsRepositoryDelete() {
        // Given
        doNothing().when(lessonRepository).deleteById(null);

        // When
        lessonService.deleteLesson(null);

        // Then
        verify(lessonRepository).deleteById(null);
    }

    // Test for constructor coverage
    @Test
    void constructor_InitializesRepositories() {
        // Given & When
        LessonService service = new LessonService(lessonRepository, courseRepository);

        // Then
        assertNotNull(service);
        // The constructor is implicitly tested by the @InjectMocks annotation
        // but this test ensures explicit coverage
    }

    // Additional edge case tests for complete coverage
    @Test
    void createLessonFromRequest_WithNullRequest_ThrowsException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            lessonService.createLessonFromRequest(null);
        });
    }

    @Test
    void createLesson_WithNullLesson_CallsRepository() {
        // Given
        when(lessonRepository.save(null)).thenReturn(null);

        // When
        Lesson result = lessonService.createLesson(null);

        // Then
        assertNull(result);
        verify(lessonRepository).save(null);
    }
}
