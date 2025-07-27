package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.model.Exercise;
import com.learncode.schoolDev.model.Submission;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.service.SubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {

    @InjectMocks
    private SubmissionController submissionController;

    @Mock
    private SubmissionService submissionService;

    private Submission submission;

    private User user;

    private Exercise exercise;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setUserId(2L);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");

        exercise = new Exercise();
        exercise.setExerciseId(3L);
        exercise.setTitle("Old Title");

        submission = new Submission();
        submission.setSubmissionId(1L);
        submission.setUser(user);
        submission.setExercise(exercise);
        submission.setCode("print('Hello')");
        submission.setCorrect(true);
    }

    @Test
    void testGetAllSubmissions() {
        List<Submission> submissions = Arrays.asList(submission, new Submission());
        when(submissionService.getAllSubmissions()).thenReturn(submissions);

        List<Submission> result = submissionController.getAllSubmissions();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(submissionService).getAllSubmissions();
    }

    @Test
    void testGetSubmissionById_Found() {
        when(submissionService.getSubmissionById(1L)).thenReturn(Optional.of(submission));

        ResponseEntity<Submission> response = submissionController.getSubmissionById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(submission, response.getBody());
        verify(submissionService).getSubmissionById(1L);
    }

    @Test
    void testGetSubmissionById_NotFound() {
        when(submissionService.getSubmissionById(42L)).thenReturn(Optional.empty());

        ResponseEntity<Submission> response = submissionController.getSubmissionById(42L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(submissionService).getSubmissionById(42L);
    }

    @Test
    void testCreateSubmission_Success() {
        when(submissionService.createSubmission(any(Submission.class))).thenReturn(submission);
    
        Submission result = submissionController.createSubmission(submission);
    
        assertEquals(submission, result);
        verify(submissionService).createSubmission(submission);
    }

    @Test
    void testCreateSubmission_Failure() {
        when(submissionService.createSubmission(any(Submission.class)))
            .thenThrow(new RuntimeException("Erreur de création"));

        Exception ex = assertThrows(RuntimeException.class, () -> submissionController.createSubmission(submission));
        assertEquals("Erreur de création", ex.getMessage());
        verify(submissionService).createSubmission(submission);
    }

    @Test
    void testUpdateSubmission_Success() {
        when(submissionService.updateSubmission(eq(1L), any(Submission.class))).thenReturn(submission);

        ResponseEntity<Submission> response = submissionController.updateSubmission(1L, submission);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(submission, response.getBody());
        verify(submissionService).updateSubmission(1L, submission);
    }

    @Test
    void testUpdateSubmission_NotFound() {
        when(submissionService.updateSubmission(eq(1L), any(Submission.class)))
                .thenThrow(new RuntimeException("Submission non trouvé avec ID : 1"));
    
        ResponseEntity<Submission> response = submissionController.updateSubmission(1L, submission);
    
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(submissionService).updateSubmission(1L, submission);
    }
    

    @Test
    void testDeleteSubmission() {
        doNothing().when(submissionService).deleteSubmission(1L);

        ResponseEntity<?> response = submissionController.deleteSubmission(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(submissionService).deleteSubmission(1L);
    }

    @Test
    void testGetSubmissionsByUser() {
        List<Submission> userSubmissions = Arrays.asList(submission, new Submission());
        when(submissionService.getSubmissionsByUser(2L)).thenReturn(userSubmissions);

        List<Submission> result = submissionController.getSubmissionsByUser(2L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(submissionService).getSubmissionsByUser(2L);
    }

    @Test
    void testGetSubmissionsByExercise() {
        List<Submission> exerciseSubmissions = Arrays.asList(submission, new Submission());
        when(submissionService.getSubmissionsByExercise(3L)).thenReturn(exerciseSubmissions);

        List<Submission> result = submissionController.getSubmissionsByExercise(3L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(submissionService).getSubmissionsByExercise(3L);
    }
}
