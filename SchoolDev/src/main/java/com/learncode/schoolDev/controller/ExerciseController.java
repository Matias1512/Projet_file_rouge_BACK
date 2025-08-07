package com.learncode.schoolDev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learncode.schoolDev.dto.ExerciseResponse;
import com.learncode.schoolDev.dto.ExerciseListResponse;
import com.learncode.schoolDev.model.Exercise;
import com.learncode.schoolDev.model.QcmProposition;
import com.learncode.schoolDev.service.ExerciseService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exercises")
@Tag(name = "Exercises", description = "Gestion des exercices")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    @Operation(summary = "Liste tous les exercices", description = "Retourne la liste complète des exercices")
    public List<Exercise> getAllExercises() {
        return exerciseService.getAllExercises();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un exercice par ID", description = "Retourne les détails d'un exercice spécifique avec propositions QCM si applicable")
    public ResponseEntity<ExerciseResponse> getExerciseById(@PathVariable Long id) {
        Optional<ExerciseResponse> exercise = exerciseService.getExerciseByIdAsDto(id);
        return exercise.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Obtenir les exercices par leçon", description = "Retourne la liste des exercices d'une leçon spécifique (format optimisé sans starterCode/testCases)")
    public List<ExerciseListResponse> getExercisesByLesson(@PathVariable Long lessonId) {
        return exerciseService.getExercisesByLessonAsDto(lessonId);
    }

    @PostMapping
    @Operation(summary = "Créer un nouvel exercice", description = "Ajoute un nouvel exercice à la base de données. Utilise 'lessonId' pour la leçon et 'propositions' pour les exercices QCM.")
    public ResponseEntity<Exercise> createExercise(@Valid @RequestBody Exercise exercise) {
        try {
            Exercise savedExercise = exerciseService.createExercise(exercise);
            return ResponseEntity.ok(savedExercise);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un exercice", description = "Met à jour les informations d'un exercice existant")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long id, @RequestBody Exercise updatedExercise) {
        try {
            return ResponseEntity.ok(exerciseService.updateExercise(id, updatedExercise));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un exercice", description = "Supprime un exercice de la base de données")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/qcm-propositions")
    @Operation(summary = "Obtenir les propositions QCM", description = "Retourne les propositions d'un exercice QCM")
    public List<QcmProposition> getQcmPropositions(@PathVariable Long id) {
        return exerciseService.getQcmPropositionsByExercise(id);
    }
    
    @GetMapping("/{id}/qcm-correct-propositions")
    @Operation(summary = "Obtenir les bonnes réponses QCM", description = "Retourne les bonnes réponses d'un exercice QCM")
    public List<QcmProposition> getCorrectPropositions(@PathVariable Long id) {
        return exerciseService.getCorrectPropositionsByExercise(id);
    }
}
