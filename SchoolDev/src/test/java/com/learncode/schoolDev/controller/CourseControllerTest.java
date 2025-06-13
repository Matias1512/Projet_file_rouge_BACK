package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.model.Course;
import com.learncode.schoolDev.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    private Course course1;
    private Course course2;

    @BeforeEach
    void setup() {
        course1 = new Course(1L, "Intro to Java", "Java", "EASY");
        course2 = new Course(2L, "Advanced Java", "Java", "HARD");
    }

    @Test
    void testGetAllCourses() {
        when(courseService.getAllCourses()).thenReturn(List.of(course1, course2));

        List<Course> result = courseController.getAllCourses();

        assertEquals(2, result.size());
        assertEquals("Intro to Java", result.get(0).getTitle());
    }

    @Test
    void testGetCourseById_Found() {
        when(courseService.getCourseById(1L)).thenReturn(Optional.of(course1));

        ResponseEntity<Course> response = courseController.getCourseById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Intro to Java", response.getBody().getTitle());
    }

    @Test
    void testGetCourseById_NotFound() {
        when(courseService.getCourseById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Course> response = courseController.getCourseById(99L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testCreateCourse() {
        Course input = new Course(3L, "New Course", "Python", "NORMAL");
        Course saved = new Course(3L, "New Course", "Python", "NORMAL");

        when(courseService.createCourse(any(Course.class))).thenReturn(saved);

        Course result = courseController.createCourse(input);

        assertEquals("New Course", result.getTitle());
        assertEquals("Python", result.getLanguage());
        assertEquals(3L, result.getCourseId());
    }

    @Test
    void testUpdateCourse_Success() {
        Course update = new Course(1L, "Updated Course", "Java", "HARD");
        Course updated = new Course(1L, "Updated Course", "Java", "HARD");

        when(courseService.updateCourse(eq(1L), any(Course.class))).thenReturn(updated);

        ResponseEntity<Course> response = courseController.updateCourse(1L, update);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Updated Course", response.getBody().getTitle());
    }

    @Test
    void testUpdateCourse_NotFound() {
        when(courseService.updateCourse(eq(99L), any(Course.class)))
                .thenThrow(new RuntimeException("Course not found"));

        ResponseEntity<Course> response = courseController.updateCourse(99L, new Course());

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testDeleteCourse() {
        doNothing().when(courseService).deleteCourse(1L);

        ResponseEntity<Void> response = courseController.deleteCourse(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(courseService, times(1)).deleteCourse(1L);
    }
}
