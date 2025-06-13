package com.learncode.schooldev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learncode.schooldev.model.Exercise;
import com.learncode.schooldev.service.ExerciseService;

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
    @Operation(summary = "Obtenir un exercice par ID", description = "Retourne les détails d'un exercice spécifique")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Long id) {
        Optional<Exercise> exercise = exerciseService.getExerciseById(id);
        return exercise.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Obtenir les exercices par leçon", description = "Retourne la liste des exercices d'une leçon spécifique")
    public List<Exercise> getExercisesByLesson(@PathVariable Long lessonId) {
        return exerciseService.getExercisesByLesson(lessonId);
    }

    @PostMapping
    @Operation(summary = "Créer un nouvel exercice", description = "Ajoute un nouvel exercice à la base de données")
    public Exercise createExercise(@Valid @RequestBody Exercise exercise) {
        return exerciseService.createExercise(exercise);
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
}
