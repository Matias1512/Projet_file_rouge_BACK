package com.learncode.schoolDev.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProgressTest {

    private Progress progress;
    private User user;
    private Course course;

    @BeforeEach
    void setUp() {
        progress = new Progress();
        
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");

        course = new Course();
        course.setCourseId(1L);
        course.setTitle("Test Course");
    }

    @Test
    void testProgressIdGetterAndSetter() {
        Long progressId = 1L;
        progress.setProgressId(progressId);
        assertEquals(progressId, progress.getProgressId());
    }

    @Test
    void testCurrentLessonIdGetterAndSetter() {
        Long currentLessonId = 5L;
        progress.setCurrentLessonId(currentLessonId);
        assertEquals(currentLessonId, progress.getCurrentLessonId());
    }

    @Test
    void testPercentageCompletedGetterAndSetter() {
        double percentage = 75.5;
        progress.setPercentageCompleted(percentage);
        assertEquals(percentage, progress.getPercentageCompleted());
    }

    @Test
    void testLastUpdatedGetter() {
        LocalDateTime before = LocalDateTime.now();
        progress.onUpdate();
        LocalDateTime after = LocalDateTime.now();
        
        LocalDateTime lastUpdated = progress.getLastUpdated();
        assertNotNull(lastUpdated);
        assertTrue(lastUpdated.isAfter(before) || lastUpdated.isEqual(before));
        assertTrue(lastUpdated.isBefore(after) || lastUpdated.isEqual(after));
    }

    @Test
    void testUserGetterAndSetter() {
        progress.setUser(user);
        assertEquals(user, progress.getUser());
    }

    @Test
    void testCourseGetterAndSetter() {
        progress.setCourse(course);
        assertEquals(course, progress.getCourse());
    }

    @Test
    void testOnUpdateMethod() {
        LocalDateTime before = LocalDateTime.now();
        progress.onUpdate();
        LocalDateTime after = LocalDateTime.now();
        
        LocalDateTime lastUpdated = progress.getLastUpdated();
        assertNotNull(lastUpdated);
        assertTrue(lastUpdated.isAfter(before) || lastUpdated.isEqual(before));
        assertTrue(lastUpdated.isBefore(after) || lastUpdated.isEqual(after));
    }

    @Test
    void testProgressCreationWithAllFields() {
        Long progressId = 1L;
        Long currentLessonId = 3L;
        double percentage = 80.0;

        progress.setProgressId(progressId);
        progress.setCurrentLessonId(currentLessonId);
        progress.setPercentageCompleted(percentage);
        progress.setUser(user);
        progress.setCourse(course);
        progress.onUpdate();

        assertEquals(progressId, progress.getProgressId());
        assertEquals(currentLessonId, progress.getCurrentLessonId());
        assertEquals(percentage, progress.getPercentageCompleted());
        assertEquals(user, progress.getUser());
        assertEquals(course, progress.getCourse());
        assertNotNull(progress.getLastUpdated());
    }
}