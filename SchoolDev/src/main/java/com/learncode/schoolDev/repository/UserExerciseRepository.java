package com.learncode.schoolDev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learncode.schoolDev.model.UserExercise;

import java.util.List;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Long> {
    List<UserExercise> findByUser_UserId(Long userId);
    List<UserExercise> findByExercise_ExerciseId(Long exerciceId);
}
