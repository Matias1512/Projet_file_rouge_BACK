package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learncode.schoolDev.dto.QcmPropositionRequest;
import com.learncode.schoolDev.dto.ExerciseResponse;
import com.learncode.schoolDev.dto.ExerciseListResponse;
import com.learncode.schoolDev.dto.QcmPropositionResponse;
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

    @Transactional
    public Exercise createExercise(Exercise exercise) {
        validateExercise(exercise);
        setLessonIfProvided(exercise);
        Exercise savedExercise = exerciseRepository.save(exercise);
        createQcmPropositionsIfNeeded(savedExercise, exercise.getPropositionsForApi());
        return savedExercise;
    }

    private void validateExercise(Exercise exercise) {
        if (exercise.getType() == ExerciseType.CODE) {
            validateCodeExercise(exercise);
        } else if (exercise.getType() == ExerciseType.QCM) {
            validateQcmExercise(exercise);
        }
    }

    private void validateCodeExercise(Exercise exercise) {
        if (exercise.getStarterCode() == null || exercise.getStarterCode().trim().isEmpty()) {
            throw new RuntimeException("Le code de démarrage est obligatoire pour un exercice de code");
        }
        if (exercise.getTestCases() == null || exercise.getTestCases().trim().isEmpty()) {
            throw new RuntimeException("Les cas de test sont obligatoires pour un exercice de code");
        }
        if (exercise.getPropositionsForApi() != null && !exercise.getPropositionsForApi().isEmpty()) {
            throw new RuntimeException("Un exercice de code ne doit pas avoir de propositions QCM");
        }
    }

    private void validateQcmExercise(Exercise exercise) {
        validateQcmPropositions(exercise.getPropositionsForApi());
        validateQcmHasNoCodeFields(exercise);
    }

    private void validateQcmPropositions(List<QcmPropositionRequest> propositions) {
        if (propositions == null || propositions.size() != 3) {
            throw new RuntimeException("Un exercice QCM doit avoir exactement 3 propositions");
        }
        boolean hasCorrectAnswer = propositions.stream()
            .anyMatch(QcmPropositionRequest::getIsCorrect);
        if (!hasCorrectAnswer) {
            throw new RuntimeException("Un exercice QCM doit avoir au moins une bonne réponse");
        }
    }

    private void validateQcmHasNoCodeFields(Exercise exercise) {
        if (exercise.getStarterCode() != null && !exercise.getStarterCode().trim().isEmpty()) {
            throw new RuntimeException("Un exercice QCM ne doit pas avoir de code de démarrage");
        }
        if (exercise.getTestCases() != null && !exercise.getTestCases().trim().isEmpty()) {
            throw new RuntimeException("Un exercice QCM ne doit pas avoir de cas de test");
        }
    }

    private void setLessonIfProvided(Exercise exercise) {
        if (exercise.getLessonIdForApi() != null) {
            Lesson lesson = lessonRepository.findById(exercise.getLessonIdForApi())
                .orElseThrow(() -> new RuntimeException("Leçon non trouvée avec ID : " + exercise.getLessonIdForApi()));
            exercise.setLesson(lesson);
        }
    }

    private void createQcmPropositionsIfNeeded(Exercise savedExercise, List<QcmPropositionRequest> propositions) {
        if (savedExercise.getType() == ExerciseType.QCM && propositions != null) {
            for (QcmPropositionRequest propRequest : propositions) {
                QcmProposition proposition = new QcmProposition();
                proposition.setText(propRequest.getText());
                proposition.setCorrect(propRequest.getIsCorrect());
                proposition.setExercise(savedExercise);
                qcmPropositionRepository.save(proposition);
            }
        }
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
    
    public Optional<ExerciseResponse> getExerciseByIdAsDto(Long id) {
        Optional<Exercise> exercise = exerciseRepository.findById(id);
        return exercise.map(this::convertToDto);
    }
    
    public List<ExerciseListResponse> getExercisesByLessonAsDto(Long lessonId) {
        List<Exercise> exercises = exerciseRepository.findByLesson_LessonId(lessonId);
        return exercises.stream()
            .map(this::convertToListDto)
            .toList();
    }
    
    private ExerciseResponse convertToDto(Exercise exercise) {
        ExerciseResponse dto = new ExerciseResponse(
            exercise.getExerciseId(),
            exercise.getTitle(),
            exercise.getDescription(),
            exercise.getType(),
            exercise.getStarterCode(),
            exercise.getTestCases(),
            exercise.getCreatedAt()
        );
        
        // Ajouter les informations de la leçon
        if (exercise.getLesson() != null) {
            dto.setLessonId(exercise.getLesson().getLessonId());
            dto.setLesson(exercise.getLesson());
        }
        
        // Ajouter les propositions QCM si c'est un exercice QCM
        if (exercise.getType() == ExerciseType.QCM) {
            List<QcmProposition> propositions = qcmPropositionRepository.findByExercise_ExerciseId(exercise.getExerciseId());
            List<QcmPropositionResponse> propositionDtos = propositions.stream()
                .map(prop -> new QcmPropositionResponse(prop.getPropositionId(), prop.getText(), prop.isCorrect()))
                .toList();
            dto.setPropositions(propositionDtos);
        }
        
        return dto;
    }
    
    private ExerciseListResponse convertToListDto(Exercise exercise) {
        return new ExerciseListResponse(
            exercise.getExerciseId(),
            exercise.getTitle(),
            exercise.getDescription(),
            exercise.getType(),
            exercise.getCreatedAt()
        );
    }
}
