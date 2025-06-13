package com.learncode.schooldev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learncode.schooldev.model.Submission;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUser_UserId(Long userId);
    List<Submission> findByExercise_ExerciseId(Long exerciseId);
}
