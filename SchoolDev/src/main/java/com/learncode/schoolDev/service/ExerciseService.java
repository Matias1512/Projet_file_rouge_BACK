package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learncode.schoolDev.dto.ExerciseCreateRequest;
import com.learncode.schoolDev.dto.QcmPropositionDto;
import com.learncode.schoolDev.model.Exercise;
import com.learncode.schoolDev.model.ExerciseType;
import com.learncode.schoolDev.model.QcmProposition;
import com.learncode.schoolDev.model.Lesson;
import com.learncode.schoolDev.repository.ExerciseRepository;
import com.learncode.schoolDev.repository.QcmPropositionRepository;
import com.learncode.schoolDev.repository.LessonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final QcmPropositionRepository qcmPropositionRepository;
    private final LessonRepository lessonRepository;

    public ExerciseService(ExerciseRepository exerciseRepository, 
                          QcmPropositionRepository qcmPropositionRepository,
                          LessonRepository lessonRepository) {
        this.exerciseRepository = exerciseRepository;
        this.qcmPropositionRepository = qcmPropositionRepository;
        this.lessonRepository = lessonRepository;
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Optional<Exercise> getExerciseById(Long id) {
        return exerciseRepository.findById(id);
    }

    public List<Exercise> getExercisesByLesson(Long lessonId) {
        return exerciseRepository.findByLesson_LessonId(lessonId);
    }

    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }
    
    @Transactional
    public Exercise createExerciseFromRequest(ExerciseCreateRequest request) {
        // Validation selon le type
        if (request.getType() == ExerciseType.CODE) {
            if (request.getStarterCode() == null || request.getStarterCode().trim().isEmpty()) {
                throw new RuntimeException("Le code de démarrage est obligatoire pour un exercice de code");
            }
            if (request.getTestCases() == null || request.getTestCases().trim().isEmpty()) {
                throw new RuntimeException("Les cas de test sont obligatoires pour un exercice de code");
            }
        } else if (request.getType() == ExerciseType.QCM) {
            if (request.getPropositions() == null || request.getPropositions().size() != 3) {
                throw new RuntimeException("Un exercice QCM doit avoir exactement 3 propositions");
            }
            // Vérifier qu'au moins une proposition est correcte
            boolean hasCorrectAnswer = request.getPropositions().stream()
                .anyMatch(QcmPropositionDto::getIsCorrect);
            if (!hasCorrectAnswer) {
                throw new RuntimeException("Un exercice QCM doit avoir au moins une bonne réponse");
            }
        }
        
        // Récupérer la leçon
        Lesson lesson = lessonRepository.findById(request.getLessonId())
            .orElseThrow(() -> new RuntimeException("Leçon non trouvée avec ID : " + request.getLessonId()));
        
        // Créer l'exercice
        Exercise exercise = new Exercise();
        exercise.setTitle(request.getTitle());
        exercise.setDescription(request.getDescription());
        exercise.setType(request.getType());
        exercise.setLesson(lesson);
        
        if (request.getType() == ExerciseType.CODE) {
            exercise.setStarterCode(request.getStarterCode());
            exercise.setTestCases(request.getTestCases());
        }
        
        // Sauvegarder l'exercice
        Exercise savedExercise = exerciseRepository.save(exercise);
        
        // Créer les propositions QCM si nécessaire
        if (request.getType() == ExerciseType.QCM && request.getPropositions() != null) {
            for (QcmPropositionDto propDto : request.getPropositions()) {
                QcmProposition proposition = new QcmProposition();
                proposition.setText(propDto.getText());
                proposition.setCorrect(propDto.getIsCorrect());
                proposition.setExercise(savedExercise);
                qcmPropositionRepository.save(proposition);
            }
        }
        
        return savedExercise;
    }

    public Exercise updateExercise(Long id, Exercise updatedExercise) {
        return exerciseRepository.findById(id)
                .map(exercise -> {
                    exercise.setTitle(updatedExercise.getTitle());
                    exercise.setDescription(updatedExercise.getDescription());
                    exercise.setType(updatedExercise.getType());
                    exercise.setStarterCode(updatedExercise.getStarterCode());
                    exercise.setTestCases(updatedExercise.getTestCases());
                    return exerciseRepository.save(exercise);
                })
                .orElseThrow(() -> new RuntimeException("Exercice non trouvé avec ID : " + id));
    }

    @Transactional
    public void deleteExercise(Long id) {
        // Supprimer d'abord les propositions QCM liées
        qcmPropositionRepository.deleteAll(qcmPropositionRepository.findByExercise_ExerciseId(id));
        // Puis supprimer l'exercice
        exerciseRepository.deleteById(id);
    }
    
    public List<QcmProposition> getQcmPropositionsByExercise(Long exerciseId) {
        return qcmPropositionRepository.findByExercise_ExerciseId(exerciseId);
    }
    
    public List<QcmProposition> getCorrectPropositionsByExercise(Long exerciseId) {
        return qcmPropositionRepository.findCorrectPropositionsByExerciseId(exerciseId);
    }
}
