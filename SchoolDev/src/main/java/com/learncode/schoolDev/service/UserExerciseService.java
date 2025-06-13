package com.learncode.schooldev.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.learncode.schooldev.model.UserExercise;
import com.learncode.schooldev.repository.UserExerciseRepository;

@Service
public class UserExerciseService {

    private final UserExerciseRepository repository;

    public UserExerciseService(UserExerciseRepository repository) {
        this.repository = repository;
    }

    public UserExercise save(UserExercise userExercise) {
        return repository.save(userExercise);
    }

    public List<UserExercise> getAll() {
        return repository.findAll();
    }

    public List<UserExercise> getByUserId(Long userId) {
        return repository.findByUser_UserId(userId);
    }

    public List<UserExercise> getByExerciseId(Long exerciseId) {
        return repository.findByExercise_ExerciseId(exerciseId);
    }

    public List<UserExercise> getAllSuccessfulExercice(Long userId) {
        return repository.findByUser_UserId(userId).stream()
                .filter(userExercise -> userExercise.getSuccess() == true)
                .toList();
    }

    public List<UserExercise> getSuccessfulExercicebyLanguage(Long userId, String language) {
        return repository.findByUser_UserId(userId).stream()
                .filter(userExercise -> userExercise.getExercise().getLesson().getCourse().getLanguage() == language 
                && userExercise.getSuccess() == true)
                .toList();
    }
}