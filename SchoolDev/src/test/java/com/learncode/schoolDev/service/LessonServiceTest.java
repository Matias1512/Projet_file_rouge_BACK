package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.Lesson;
import com.learncode.schoolDev.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private LessonService lessonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllLessons_ReturnsAllLessons() {
        List<Lesson> lessons = Arrays.asList(new Lesson(), new Lesson());
        when(lessonRepository.findAll()).thenReturn(lessons);

        List<Lesson> result = lessonService.getAllLessons();

        assertEquals(2, result.size());
        verify(lessonRepository).findAll();
    }

    @Test
    void getLessonById_Found() {
        Lesson lesson = new Lesson();
        lesson.setLessonId(1L);
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        Optional<Lesson> result = lessonService.getLessonById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getLessonId());
        verify(lessonRepository).findById(1L);
    }

    @Test
    void getLessonById_NotFound() {
        when(lessonRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Lesson> result = lessonService.getLessonById(99L);

        assertFalse(result.isPresent());
        verify(lessonRepository).findById(99L);
    }

    @Test
    void createLesson_ReturnsSavedLesson() {
        Lesson lesson = new Lesson();
        lesson.setTitle("New Lesson");
        when(lessonRepository.save(lesson)).thenReturn(lesson);

        Lesson result = lessonService.createLesson(lesson);

        assertEquals("New Lesson", result.getTitle());
        verify(lessonRepository).save(lesson);
    }

    @Test
    void updateLesson_Success() {
        Lesson existing = new Lesson();
        existing.setLessonId(1L);
        existing.setTitle("Old Title");

        Lesson updated = new Lesson();
        updated.setTitle("New Title");

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(inv -> inv.getArgument(0));

        Lesson result = lessonService.updateLesson(1L, updated);

        assertEquals("New Title", result.getTitle());
        verify(lessonRepository).findById(1L);
        verify(lessonRepository).save(existing);
    }

    @Test
    void updateLesson_NotFound_Throws() {
        Lesson updated = new Lesson();
        when(lessonRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            lessonService.updateLesson(2L, updated);
        });
        assertTrue(ex.getMessage().contains("Leçon non trouvée"));
        verify(lessonRepository).findById(2L);
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void deleteLesson_CallsRepository() {
        doNothing().when(lessonRepository).deleteById(7L);

        lessonService.deleteLesson(7L);

        verify(lessonRepository).deleteById(7L);
    }
}
