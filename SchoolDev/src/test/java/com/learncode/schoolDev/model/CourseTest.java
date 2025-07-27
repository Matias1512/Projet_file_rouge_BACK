package com.learncode.schoolDev.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    void testOnCreateSetsCreatedAt() {
        Course course = new Course();
        course.onCreate(); // Appelle la méthode @PrePersist
        
        assertNotNull(course.getCreatedAt());
        // Optionnel : vérifier que la date est proche de maintenant
        assertTrue(course.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(2)));
    }

    @Test
    void testGetCreatedAt() {
        Course course = new Course();
        course.onCreate();
        LocalDateTime date = course.getCreatedAt();
        assertNotNull(date);
    }

}
