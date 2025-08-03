package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.model.*;
import com.learncode.schoolDev.service.UserExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserExerciseControllerTest {

    @InjectMocks
    private UserExerciseController userExerciseController;

    @Mock
    private UserExerciseService service;

    private UserExercise userExercise;
    private User user;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);

        exercise = new Exercise();
        exercise.setExerciseId(2L);

        userExercise = new UserExercise();
        userExercise.setUser(user);
        userExercise.setExercise(exercise);
        userExercise.setSuccess(true);
    }

    @Test
    void testCreateReturnsSavedUserExercise() {
        when(service.save(any(UserExercise.class))).thenReturn(userExercise);

        UserExercise input = new UserExercise();
        input.setUser(user);
        input.setExercise(exercise);
        input.setSuccess(true);

        UserExercise result = userExerciseController.create(input);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(exercise, result.getExercise());
        assertTrue(result.getSuccess());
        verify(service).save(input);
    }

    @Test
    void testGetAllReturnsList() {
        List<UserExercise> list = Arrays.asList(userExercise, new UserExercise());
        when(service.getAll()).thenReturn(list);

        List<UserExercise> result = userExerciseController.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(service).getAll();
    }

    @Test
    void testGetByUserReturnsList() {
        when(service.getByUserId(1L)).thenReturn(List.of(userExercise));

        List<UserExercise> result = userExerciseController.getByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user, result.get(0).getUser());
        verify(service).getByUserId(1L);
    }

    @Test
    void testGetByExerciseReturnsList() {
        when(service.getByExerciseId(2L)).thenReturn(List.of(userExercise));

        List<UserExercise> result = userExerciseController.getByExercise(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(exercise, result.get(0).getExercise());
        verify(service).getByExerciseId(2L);
    }

    @Test
    void testGetByUserAndExercise_Found() {
        when(service.findByUserIdAndExerciseId(1L, 2L)).thenReturn(Optional.of(userExercise));

        ResponseEntity<UserExercise> result = userExerciseController.getByUserAndExercise(1L, 2L);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(user, result.getBody().getUser());
        assertEquals(exercise, result.getBody().getExercise());
        verify(service).findByUserIdAndExerciseId(1L, 2L);
    }

    @Test
    void testGetByUserAndExercise_NotFound() {
        when(service.findByUserIdAndExerciseId(1L, 2L)).thenReturn(Optional.empty());

        ResponseEntity<UserExercise> result = userExerciseController.getByUserAndExercise(1L, 2L);

        assertNotNull(result);
        assertEquals(404, result.getStatusCode().value());
        assertNull(result.getBody());
        verify(service).findByUserIdAndExerciseId(1L, 2L);
    }

    @Test
    void testCreateWithIds() {
        when(service.createUserExercise(1L, 2L, true)).thenReturn(userExercise);

        UserExercise result = userExerciseController.createWithIds(1L, 2L, true);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(exercise, result.getExercise());
        assertTrue(result.getSuccess());
        verify(service).createUserExercise(1L, 2L, true);
    }

    @Test
    void testUpdateSuccess_Found() {
        when(service.updateSuccess(1L, true)).thenReturn(Optional.of(userExercise));

        ResponseEntity<UserExercise> result = userExerciseController.updateSuccess(1L, true);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(user, result.getBody().getUser());
        assertEquals(exercise, result.getBody().getExercise());
        verify(service).updateSuccess(1L, true);
    }

    @Test
    void testUpdateSuccess_NotFound() {
        when(service.updateSuccess(999L, true)).thenReturn(Optional.empty());

        ResponseEntity<UserExercise> result = userExerciseController.updateSuccess(999L, true);

        assertNotNull(result);
        assertEquals(404, result.getStatusCode().value());
        assertNull(result.getBody());
        verify(service).updateSuccess(999L, true);
    }
}
