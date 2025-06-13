package com.learncode.schooldev.service;

import org.springframework.stereotype.Service;

import com.learncode.schooldev.model.Progress;
import com.learncode.schooldev.repository.ProgressRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProgressService {
    private final ProgressRepository progressRepository;

    public ProgressService(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public List<Progress> getAllProgress() {
        return progressRepository.findAll();
    }

    public Optional<Progress> getProgressById(Long id) {
        return progressRepository.findById(id);
    }

    public List<Progress> getProgressByUser(Long userId) {
        return progressRepository.findByUser_UserId(userId);
    }

    public List<Progress> getProgressByCourse(Long courseId) {
        return progressRepository.findByCourse_CourseId(courseId);
    }

    public Optional<Progress> getProgressByUserAndCourse(Long userId, Long courseId) {
        return progressRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId);
    }

    public Progress createProgress(Progress progress) {
        return progressRepository.save(progress);
    }

    public Progress updateProgress(Long id, Progress updatedProgress) {
        return progressRepository.findById(id)
                .map(progress -> {
                    progress.setCurrentLessonId(updatedProgress.getCurrentLessonId());
                    progress.setPercentageCompleted(updatedProgress.getPercentageCompleted());
                    progress.setUser(updatedProgress.getUser());
                    progress.setCourse(updatedProgress.getCourse());
                    return progressRepository.save(progress);
                })
                .orElseThrow(() -> new RuntimeException("Progress non trouvé avec ID : " + id));
    }

    public void deleteProgress(Long id) {
        progressRepository.deleteById(id);
    }
}
