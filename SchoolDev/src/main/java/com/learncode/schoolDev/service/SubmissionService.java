package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;

import com.learncode.schoolDev.model.Submission;
import com.learncode.schoolDev.repository.SubmissionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
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

    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public Submission updateSubmission(Long id, Submission updatedSubmission) {
        return submissionRepository.findById(id)
                .map(submission -> {
                    submission.setCode(updatedSubmission.getCode());
                    submission.setCorrect(updatedSubmission.isCorrect());
                    submission.setUser(updatedSubmission.getUser());
                    submission.setExercise(updatedSubmission.getExercise());
                    return submissionRepository.save(submission);
                })
                .orElseThrow(() -> new RuntimeException("Submission non trouvée avec ID : " + id));
    }

    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }
}

