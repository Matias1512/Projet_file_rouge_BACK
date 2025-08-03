package com.learncode.schoolDev.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.learncode.schoolDev.model.UserExercise;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.Exercise;
import com.learncode.schoolDev.repository.UserExerciseRepository;
import com.learncode.schoolDev.repository.UserRepository;
import com.learncode.schoolDev.repository.ExerciseRepository;

@Service
public class UserExerciseService {

    private final UserExerciseRepository repository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    public UserExerciseService(UserExerciseRepository repository, UserRepository userRepository, ExerciseRepository exerciseRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public UserExercise save(UserExercise userExercise) {
        return repository.save(userExercise);
    }

    public UserExercise createUserExercise(Long userId, Long exerciseId, Boolean success) {
        // Vérifier si une entrée existe déjà en premier
        if (repository.existsByUser_UserIdAndExercise_ExerciseId(userId, exerciseId)) {
            throw new RuntimeException("Un UserExercise existe déjà pour cet utilisateur et cet exercice");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new RuntimeException("Exercice non trouvé"));
        
        UserExercise userExercise = new UserExercise();
        userExercise.setUser(user);
        userExercise.setExercise(exercise);
        userExercise.setSuccess(success);
        userExercise.setCompletedAt(LocalDateTime.now());
        
        return repository.save(userExercise);
    }

    public UserExercise updateOrCreateUserExercise(Long userId, Long exerciseId, Boolean success) {
        // Vérifier si une entrée existe déjà
        return repository.findByUser_UserIdAndExercise_ExerciseId(userId, exerciseId)
                .map(existing -> {
                    // Mettre à jour l'entrée existante
                    existing.setSuccess(success);
                    existing.setCompletedAt(LocalDateTime.now());
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    // Créer une nouvelle entrée sans vérification (car on sait qu'elle n'existe pas)
                    User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                    Exercise exercise = exerciseRepository.findById(exerciseId)
                        .orElseThrow(() -> new RuntimeException("Exercice non trouvé"));
                    
                    UserExercise userExercise = new UserExercise();
                    userExercise.setUser(user);
                    userExercise.setExercise(exercise);
                    userExercise.setSuccess(success);
                    userExercise.setCompletedAt(LocalDateTime.now());
                    
                    return repository.save(userExercise);
                });
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

    public Optional<UserExercise> findByUserIdAndExerciseId(Long userId, Long exerciseId) {
        return repository.findByUser_UserIdAndExercise_ExerciseId(userId, exerciseId);
    }

    public List<UserExercise> getAllSuccessfulExercice(Long userId) {
        return repository.findByUser_UserId(userId).stream()
                .filter(UserExercise::getSuccess)
                .toList();
    }

    public List<UserExercise> getSuccessfulExercicebyLanguage(Long userId, String language) {
        return repository.findByUser_UserId(userId).stream()
                .filter(userExercise -> userExercise.getExercise().getLesson().getCourse().getLanguage().equals(language) 
                && userExercise.getSuccess())
                .toList();
    }

    public Optional<UserExercise> updateSuccess(Long id, Boolean success) {
        return repository.findById(id)
                .map(userExercise -> {
                    userExercise.setSuccess(success);
                    userExercise.setCompletedAt(LocalDateTime.now());
                    return repository.save(userExercise);
                });
    }
}