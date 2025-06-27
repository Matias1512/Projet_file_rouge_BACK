package com.learncode.schoolDev.service;

import com.learncode.schoolDev.enums.DifficultyLevel;
import com.learncode.schoolDev.model.Course;
import com.learncode.schoolDev.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    private CourseRepository courseRepository;
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseService = new CourseService(courseRepository);
    }

    @Test
    void getAllCourses_returnsAllCourses() {
        Course c1 = new Course();
        c1.setCourseId(1L);
        c1.setTitle("Java");
        c1.setLanguage("FR");
        c1.setDifficultyLevel(DifficultyLevel.EASY);
        Course c2 = new Course();
        c2.setCourseId(2L);
        c2.setTitle("Python");
        c2.setLanguage("EN");
        c2.setDifficultyLevel(DifficultyLevel.NORMAL);

        List<Course> courses = Arrays.asList(c1, c2);

        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.getAllCourses();

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getTitle());
        verify(courseRepository).findAll();
    }

    @Test
    void getCourseById_returnsCourseIfExists() {
        Course course = new Course();
        course.setCourseId(1L);
        course.setTitle("Java");
        course.setLanguage("FR");
        course.setDifficultyLevel(DifficultyLevel.EASY);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Optional<Course> result = courseService.getCourseById(1L);

        assertTrue(result.isPresent());
        assertEquals("Java", result.get().getTitle());
        verify(courseRepository).findById(1L);
    }

    @Test
    void getCourseById_returnsEmptyIfNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Course> result = courseService.getCourseById(1L);

        assertFalse(result.isPresent());
        verify(courseRepository).findById(1L);
    }

    @Test
    void createCourse_savesCourse() {
        Course course = new Course();
        course.setTitle("Java");
        course.setLanguage("FR");
        course.setDifficultyLevel(DifficultyLevel.EASY);
        Course savedCourse = new Course();
        savedCourse.setCourseId(1L);
        savedCourse.setTitle("Java");
        savedCourse.setLanguage("FR");
        savedCourse.setDifficultyLevel(DifficultyLevel.EASY);

        when(courseRepository.save(course)).thenReturn(savedCourse);

        Course result = courseService.createCourse(course);

        assertEquals(1L, result.getCourseId());
        verify(courseRepository).save(course);
    }

    @Test
    void updateCourse_updatesAndReturnsCourse() {
        Course existing = new Course();
        existing.setCourseId(1L);
        existing.setTitle("OldTitle");
        existing.setLanguage("EN");
        existing.setDifficultyLevel(DifficultyLevel.NORMAL);

        Course updates = new Course();
        updates.setTitle("NewTitle");
        updates.setLanguage("FR");
        updates.setDifficultyLevel(DifficultyLevel.HARD);

        Course saved = new Course();
        saved.setCourseId(1L);
        saved.setTitle("NewTitle");
        saved.setLanguage("FR");
        saved.setDifficultyLevel(DifficultyLevel.HARD);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.save(any(Course.class))).thenReturn(saved);

        Course result = courseService.updateCourse(1L, updates);

        assertEquals("NewTitle", result.getTitle());
        assertEquals("FR", result.getLanguage());
        assertEquals(DifficultyLevel.HARD, result.getDifficultyLevel());
        verify(courseRepository).findById(1L);
        verify(courseRepository).save(existing);
    }

    @Test
    void updateCourse_throwsIfNotFound() {
        Course updates = new Course();
        updates.setTitle("NewTitle");
        updates.setLanguage("FR");
        updates.setDifficultyLevel(DifficultyLevel.HARD);
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> courseService.updateCourse(1L, updates)
        );
        assertTrue(ex.getMessage().contains("Course non trouvé"));
        verify(courseRepository).findById(1L);
        verify(courseRepository, never()).save(any());
    }

    @Test
    void deleteCourse_deletesById() {
        doNothing().when(courseRepository).deleteById(1L);

        courseService.deleteCourse(1L);

        verify(courseRepository).deleteById(1L);
    }
}
