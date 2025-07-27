package com.learncode.schoolDev.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionTest {

    private Submission submission;
    private User user;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        submission = new Submission();
        
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        
        exercise = new Exercise();
        exercise.setExerciseId(1L);
        exercise.setTitle("Test Exercise");
    }

    @Test
    void testSubmissionIdGetterAndSetter() {
        Long submissionId = 1L;
        submission.setSubmissionId(submissionId);
        assertEquals(submissionId, submission.getSubmissionId());
    }

    @Test
    void testCodeGetterAndSetter() {
        String code = "public class Test { }";
        submission.setCode(code);
        assertEquals(code, submission.getCode());
    }

    @Test
    void testIsCorrectGetterAndSetter() {
        submission.setCorrect(true);
        assertTrue(submission.isCorrect());
        
        submission.setCorrect(false);
        assertFalse(submission.isCorrect());
    }

    @Test
    void testSubmittedAtGetter() {
        // Initialement null
        assertNull(submission.getSubmittedAt());
        
        // Après appel de onSubmit()
        submission.onSubmit();
        assertNotNull(submission.getSubmittedAt());
        assertTrue(submission.getSubmittedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testUserGetterAndSetter() {
        submission.setUser(user);
        assertEquals(user, submission.getUser());
    }

    @Test
    void testExerciseGetterAndSetter() {
        submission.setExercise(exercise);
        assertEquals(exercise, submission.getExercise());
    }

    @Test
    void testOnSubmitMethod() {
        // Vérifier que submittedAt est null avant onSubmit
        assertNull(submission.getSubmittedAt());
        
        LocalDateTime before = LocalDateTime.now();
        submission.onSubmit();
        LocalDateTime after = LocalDateTime.now();
        
        LocalDateTime submittedAt = submission.getSubmittedAt();
        assertNotNull(submittedAt);
        assertTrue(submittedAt.isAfter(before) || submittedAt.isEqual(before));
        assertTrue(submittedAt.isBefore(after) || submittedAt.isEqual(after));
    }

    @Test
    void testSubmissionCreationWithAllFields() {
        Long submissionId = 1L;
        String code = "System.out.println('Hello World');";
        boolean isCorrect = true;

        submission.setSubmissionId(submissionId);
        submission.setCode(code);
        submission.setCorrect(isCorrect);
        submission.setUser(user);
        submission.setExercise(exercise);
        submission.onSubmit();

        assertEquals(submissionId, submission.getSubmissionId());
        assertEquals(code, submission.getCode());
        assertEquals(isCorrect, submission.isCorrect());
        assertEquals(user, submission.getUser());
        assertEquals(exercise, submission.getExercise());
        assertNotNull(submission.getSubmittedAt());
    }

    @Test
    void testSubmissionDefaultValues() {
        // Vérifier les valeurs par défaut
        assertNull(submission.getSubmissionId());
        assertNull(submission.getCode());
        assertFalse(submission.isCorrect()); // boolean par défaut = false
        assertNull(submission.getSubmittedAt());
        assertNull(submission.getUser());
        assertNull(submission.getExercise());
    }

    @Test
    void testOnSubmitCallsSetCurrentTime() {
        // Tester que onSubmit() définit submittedAt à l'heure actuelle
        LocalDateTime beforeSubmit = LocalDateTime.now().minusSeconds(1);
        submission.onSubmit();
        LocalDateTime afterSubmit = LocalDateTime.now().plusSeconds(1);
        
        LocalDateTime submittedAt = submission.getSubmittedAt();
        assertNotNull(submittedAt);
        assertTrue(submittedAt.isAfter(beforeSubmit));
        assertTrue(submittedAt.isBefore(afterSubmit));
    }

    @Test
    void testMultipleOnSubmitCalls() {
        // Premier appel
        submission.onSubmit();
        LocalDateTime firstSubmission = submission.getSubmittedAt();
        assertNotNull(firstSubmission);
        
        // Deuxième appel immédiat
        submission.onSubmit();
        LocalDateTime secondSubmission = submission.getSubmittedAt();
        
        // Le timestamp doit être égal ou après le premier
        assertTrue(secondSubmission.isAfter(firstSubmission) || secondSubmission.isEqual(firstSubmission));
    }
}