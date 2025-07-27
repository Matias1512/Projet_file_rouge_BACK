package com.learncode.schoolDev.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LessonTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidLesson() {
        Lesson lesson = new Lesson();
        lesson.setTitle("POO Java");
        lesson.setContent("Découvrir les classes et objets");
        lesson.setOrderInCourse(1);

        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);
        assertTrue(violations.isEmpty(), "Aucune violation ne doit être détectée pour une leçon valide");
    }

    @Test
    void testBlankFieldsValidation() {
        Lesson lesson = new Lesson(); // Tous les champs sont null

        Set<ConstraintViolation<Lesson>> violations = validator.validate(lesson);

        assertEquals(2, violations.size());

        assertTrue(
            violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")),
            "Le champ title doit être obligatoire"
        );
        assertTrue(
            violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("content")),
            "Le champ content doit être obligatoire"
        );
    }

    @Test
    void testGettersAndSetters() {
        Lesson lesson = new Lesson();
        lesson.setLessonId(11L);
        lesson.setTitle("Les collections");
        lesson.setContent("Introduction aux List, Set et Map en Java");
        lesson.setOrderInCourse(2);

        // Simuler l'association avec un Course
        Course course = new Course();
        lesson.setCourse(course);

        assertEquals(11L, lesson.getLessonId());
        assertEquals("Les collections", lesson.getTitle());
        assertEquals("Introduction aux List, Set et Map en Java", lesson.getContent());
        assertEquals(2, lesson.getOrderInCourse());
        assertEquals(course, lesson.getCourse());
    }

    @Test
    void testOnCreateMethod() {
        Lesson lesson = new Lesson();
        lesson.setTitle("Test Lesson");
        lesson.setContent("Test Content");
        lesson.setOrderInCourse(1);

        // Vérifier que createdAt est null avant onCreate
        assertNull(lesson.getCreatedAt());

        // Appeler la méthode onCreate
        LocalDateTime before = LocalDateTime.now();
        lesson.onCreate();
        LocalDateTime after = LocalDateTime.now();

        // Vérifier que createdAt a été défini
        LocalDateTime createdAt = lesson.getCreatedAt();
        assertNotNull(createdAt);
        assertTrue(createdAt.isAfter(before) || createdAt.isEqual(before));
        assertTrue(createdAt.isBefore(after) || createdAt.isEqual(after));
    }

    @Test
    void testGetCreatedAt() {
        Lesson lesson = new Lesson();
        
        // Initialement null
        assertNull(lesson.getCreatedAt());
        
        // Après onCreate
        lesson.onCreate();
        assertNotNull(lesson.getCreatedAt());
    }
}
