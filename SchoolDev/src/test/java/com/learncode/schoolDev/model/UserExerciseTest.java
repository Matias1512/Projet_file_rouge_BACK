package com.learncode.schoolDev.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserExerciseTest {

    @Test
    void testGettersAndSetters() {
        UserExercise ue = new UserExercise();

        User user = new User();
        Exercise ex = new Exercise();
        LocalDateTime now = LocalDateTime.now();

        ue.setId(10L);
        ue.setUser(user);
        ue.setExercise(ex);
        ue.setCompletedAt(now);
        ue.setSuccess(true);

        assertEquals(10L, ue.getId());
        assertEquals(user, ue.getUser());
        assertEquals(ex, ue.getExercise());
        assertEquals(now, ue.getCompletedAt());
        assertTrue(ue.getSuccess());
    }

    @Test
    void testIdAndCompletedAtGettersSetters() {
        UserExercise ue = new UserExercise();

        // Test setId / getId
        ue.setId(123L);
        assertEquals(123L, ue.getId());

        // Test setCompletedAt / getCompletedAt
        LocalDateTime now = LocalDateTime.now();
        ue.setCompletedAt(now);
        assertEquals(now, ue.getCompletedAt());
    }

}
