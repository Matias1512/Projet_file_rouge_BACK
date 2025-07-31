package com.learncode.schoolDev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.learncode.schoolDev.model.Submission;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUser_UserId(Long userId);
    List<Submission> findByExercise_ExerciseId(Long exerciseId);
    
    @Query("SELECT COUNT(DISTINCT s.exercise.exerciseId) FROM Submission s " +
           "WHERE s.user.userId = :userId AND s.isCorrect = true " +
           "AND s.exercise.lesson.course.courseId = :courseId")
    long countCompletedExercisesByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    @Query("SELECT s FROM Submission s WHERE s.user.userId = :userId " +
           "AND s.exercise.exerciseId = :exerciseId AND s.isCorrect = true")
    List<Submission> findCorrectSubmissionsByUserAndExercise(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);
}
