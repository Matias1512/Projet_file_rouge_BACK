package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learncode.schoolDev.model.Submission;
import com.learncode.schoolDev.repository.SubmissionRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ProgressService progressService;
    private final BadgeEventService badgeEventService;

    public SubmissionService(SubmissionRepository submissionRepository, ProgressService progressService,
                           BadgeEventService badgeEventService) {
        this.submissionRepository = submissionRepository;
        this.progressService = progressService;
        this.badgeEventService = badgeEventService;
    }

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public Optional<Submission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }

    public List<Submission> getSubmissionsByUser(Long userId) {
        return submissionRepository.findByUser_UserId(userId);
    }

    public List<Submission> getSubmissionsByExercise(Long exerciseId) {
        return submissionRepository.findByExercise_ExerciseId(exerciseId);
    }

    /**
     * Crée une nouvelle soumission et met à jour automatiquement la progression
     */
    public Submission createSubmission(Submission submission) {
        Submission savedSubmission = submissionRepository.save(submission);
        
        // Met à jour automatiquement la progression si la soumission est correcte
        if (savedSubmission.isCorrect()) {
            progressService.updateProgressAfterSubmission(
                savedSubmission.getUser().getUserId(), 
                savedSubmission.getExercise().getExerciseId()
            );
            
            // Déclencher l'évaluation des badges pour soumission réussie
            badgeEventService.publishSubmissionSuccess(savedSubmission.getUser());
        }
        
        return savedSubmission;
    }

    public Submission updateSubmission(Long id, Submission updatedSubmission) {
        return submissionRepository.findById(id)
                .map(submission -> {
                    boolean wasCorrect = submission.isCorrect();
                    
                    submission.setCode(updatedSubmission.getCode());
                    submission.setCorrect(updatedSubmission.isCorrect());
                    submission.setUser(updatedSubmission.getUser());
                    submission.setExercise(updatedSubmission.getExercise());
                    
                    Submission savedSubmission = submissionRepository.save(submission);
                    
                    // Met à jour la progression si le statut correct a changé
                    if (!wasCorrect && savedSubmission.isCorrect()) {
                        progressService.updateProgressAfterSubmission(
                            savedSubmission.getUser().getUserId(), 
                            savedSubmission.getExercise().getExerciseId()
                        );
                        
                        // Déclencher l'évaluation des badges pour soumission réussie
                        badgeEventService.publishSubmissionSuccess(savedSubmission.getUser());
                    }
                    
                    return savedSubmission;
                })
                .orElseThrow(() -> new RuntimeException("Submission non trouvée avec ID : " + id));
    }

    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }

    /**
     * Vérifie si un utilisateur a déjà réussi un exercice
     */
    public boolean hasUserCompletedExercise(Long userId, Long exerciseId) {
        return !submissionRepository.findCorrectSubmissionsByUserAndExercise(userId, exerciseId).isEmpty();
    }
}

