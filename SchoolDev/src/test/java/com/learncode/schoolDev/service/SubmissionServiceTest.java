package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.Exercise;
import com.learncode.schoolDev.model.Submission;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @InjectMocks
    private SubmissionService submissionService;

    @Mock
    private SubmissionRepository submissionRepository;

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
        when(submissionRepository.findAll()).thenReturn(submissions);

        List<Submission> result = submissionService.getAllSubmissions();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(submissionRepository).findAll();
    }

    @Test
    void testGetSubmissionById_Found() {
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        Optional<Submission> result = submissionService.getSubmissionById(1L);

        assertTrue(result.isPresent());
        assertEquals(submission, result.get());
        verify(submissionRepository).findById(1L);
    }

    @Test
    void testGetSubmissionById_NotFound() {
        when(submissionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Submission> result = submissionService.getSubmissionById(99L);

        assertFalse(result.isPresent());
        verify(submissionRepository).findById(99L);
    }

    @Test
    void testCreateSubmission() {
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);

        Submission input = new Submission();
        input.setUser(user);
        input.setExercise(exercise);
        input.setCode("code");
        input.setCorrect(false);

        Submission result = submissionService.createSubmission(input);

        assertNotNull(result);
        assertEquals(2L, result.getUser().getUserId());
        verify(submissionRepository).save(input);
    }

    @Test
    void testUpdateSubmission_Found() {
        Submission updated = new Submission();
        updated.setSubmissionId(1L);
        updated.setUser(user);
        updated.setExercise(exercise);
        updated.setCode("new content");
        updated.setCorrect(true);

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(Submission.class))).thenReturn(updated);

        Submission result = submissionService.updateSubmission(1L, updated);

        assertNotNull(result);
        assertEquals("new content", result.getCode());
        verify(submissionRepository).findById(1L);
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    void testUpdateSubmission_NotFound() {
        Submission updated = new Submission();
        updated.setSubmissionId(42L);

        when(submissionRepository.findById(42L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> submissionService.updateSubmission(42L, updated));
        assertTrue(ex.getMessage().toLowerCase().contains("non trouvé"));
        verify(submissionRepository).findById(42L);
        verify(submissionRepository, never()).save(any());
    }

    @Test
    void testDeleteSubmission_Success() {
        doNothing().when(submissionRepository).deleteById(1L);

        submissionService.deleteSubmission(1L);

        verify(submissionRepository).deleteById(1L);
    }

    @Test
    void testGetSubmissionsByUser() {
        Submission sub1 = new Submission();
        Submission sub2 = new Submission();
        when(submissionRepository.findByUser_UserId(42L)).thenReturn(List.of(sub1, sub2));

        List<Submission> result = submissionService.getSubmissionsByUser(42L);

        assertEquals(2, result.size());
        verify(submissionRepository).findByUser_UserId(42L);
    }

    @Test
    void testGetSubmissionsByExercise() {
        Submission sub = new Submission();
        when(submissionRepository.findByExercise_ExerciseId(99L)).thenReturn(List.of(sub));

        List<Submission> result = submissionService.getSubmissionsByExercise(99L);

        assertEquals(1, result.size());
        verify(submissionRepository).findByExercise_ExerciseId(99L);
    }
}
