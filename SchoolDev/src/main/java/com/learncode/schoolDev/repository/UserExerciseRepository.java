package com.learncode.schoolDev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.learncode.schoolDev.model.UserExercise;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Long> {
    List<UserExercise> findByUser_UserId(Long userId);
    List<UserExercise> findByExercise_ExerciseId(Long exerciceId);
    Optional<UserExercise> findByUser_UserIdAndExercise_ExerciseId(Long userId, Long exerciseId);
    boolean existsByUser_UserIdAndExercise_ExerciseId(Long userId, Long exerciseId);
    
    @Query("SELECT COUNT(DISTINCT ue.exercise.exerciseId) FROM UserExercise ue " +
           "WHERE ue.user.userId = :userId AND ue.success = true " +
           "AND ue.exercise.lesson.course.courseId = :courseId")
    long countCompletedExercisesByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    @Query("SELECT ue FROM UserExercise ue WHERE ue.user.userId = :userId " +
           "AND ue.exercise.exerciseId = :exerciseId AND ue.success = true")
    List<UserExercise> findSuccessfulUserExercisesByUserAndExercise(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);
    
    long countByUser_UserIdAndSuccess(Long userId, boolean success);
}
