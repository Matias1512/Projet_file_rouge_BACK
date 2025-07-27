package com.learncode.schoolDev.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidExercise() {
        Exercise exercise = new Exercise();
        exercise.setTitle("Boucles");
        exercise.setDescription("Écrire une boucle for");
        exercise.setStarterCode("for (int i = 0; i < 10; i++) {}");
        exercise.setTestCases("assert loop(3) == 3");

        Set<ConstraintViolation<Exercise>> violations = validator.validate(exercise);
        assertTrue(violations.isEmpty(), "Aucune violation ne doit être détectée pour un exercice valide");
    }

    @Test
    void testBlankFieldsValidation() {
        Exercise exercise = new Exercise(); // tous les champs sont null

        Set<ConstraintViolation<Exercise>> violations = validator.validate(exercise);
        assertEquals(4, violations.size(), "4 violations attendues pour les champs @NotBlank");

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("starterCode")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("testCases")));
    }

    @Test
    void testGettersAndSetters() {
        Lesson lesson = new Lesson(); // Peut être un mock si nécessaire

        Exercise exercise = new Exercise();
        exercise.setExerciseId(42L);
        exercise.setTitle("Conditions");
        exercise.setDescription("Écrire un if");
        exercise.setStarterCode("if (x > 0) {}");
        exercise.setTestCases("assert check(1) == true");
        exercise.setLesson(lesson);

        assertEquals(42L, exercise.getExerciseId());
        assertEquals("Conditions", exercise.getTitle());
        assertEquals("Écrire un if", exercise.getDescription());
        assertEquals("if (x > 0) {}", exercise.getStarterCode());
        assertEquals("assert check(1) == true", exercise.getTestCases());
        assertEquals(lesson, exercise.getLesson());
    }

    @Test
    void testOnCreate_setsCreatedAt() throws Exception {
        // Arrange
        Exercise exercise = new Exercise();

        // Act
        Method onCreateMethod = Exercise.class.getDeclaredMethod("onCreate");
        onCreateMethod.setAccessible(true); // car c'est protected
        onCreateMethod.invoke(exercise);

        // Assert
        assertNotNull(exercise.getCreatedAt(), "createdAt should be set by onCreate()");
        assertTrue(exercise.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
