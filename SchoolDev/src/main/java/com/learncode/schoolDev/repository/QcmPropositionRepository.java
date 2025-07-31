package com.learncode.schoolDev.repository;

import com.learncode.schoolDev.model.QcmProposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QcmPropositionRepository extends JpaRepository<QcmProposition, Long> {
    
    List<QcmProposition> findByExercise_ExerciseId(Long exerciseId);
    
    @Query("SELECT qp FROM QcmProposition qp WHERE qp.exercise.exerciseId = :exerciseId AND qp.isCorrect = true")
    List<QcmProposition> findCorrectPropositionsByExerciseId(@Param("exerciseId") Long exerciseId);
    
    long countByExercise_ExerciseId(Long exerciseId);
}