package com.learncode.schoolDev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learncode.schoolDev.model.UserExercise;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Long> {
    List<UserExercise> findByUser_UserId(Long userId);
    List<UserExercise> findByExercise_ExerciseId(Long exerciceId);
    Optional<UserExercise> findByUser_UserIdAndExercise_ExerciseId(Long userId, Long exerciseId);
    boolean existsByUser_UserIdAndExercise_ExerciseId(Long userId, Long exerciseId);
}
