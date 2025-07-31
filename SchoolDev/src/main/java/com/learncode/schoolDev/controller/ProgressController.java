package com.learncode.schoolDev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learncode.schoolDev.model.Progress;
import com.learncode.schoolDev.service.ProgressService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
@Tag(name = "Progress", description = "Gestion des progressions")
public class ProgressController {
    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping
    @Operation(summary = "Liste toutes les progressions", description = "Retourne la liste complète des progressions")
    public List<Progress> getAllProgress() {
        return progressService.getAllProgress();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une progression par ID", description = "Retourne les détails d'une progression spécifique")
    public ResponseEntity<Progress> getProgressById(@PathVariable Long id) {
        Optional<Progress> progress = progressService.getProgressById(id);
        return progress.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtenir les progressions d'un utilisateur", description = "Retourne la liste des progressions d'un utilisateur spécifique")
    public List<Progress> getProgressByUser(@PathVariable Long userId) {
        return progressService.getProgressByUser(userId);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Obtenir les progressions d'un cours", description = "Retourne la liste des progressions d'un cours spécifique")
    public List<Progress> getProgressByCourse(@PathVariable Long courseId) {
        return progressService.getProgressByCourse(courseId);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle progression", description = "Ajoute une nouvelle progression à la base de données")
    public Progress createProgress(@Valid @RequestBody Progress progress) {
        return progressService.createProgress(progress);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une progression", description = "Met à jour les informations d'une progression existante")
    public ResponseEntity<Progress> updateProgress(@PathVariable Long id, @RequestBody Progress updatedProgress) {
        try {
            return ResponseEntity.ok(progressService.updateProgress(id, updatedProgress));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une progression", description = "Supprime une progression de la base de données")
    public ResponseEntity<Void> deleteProgress(@PathVariable Long id) {
        progressService.deleteProgress(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/user/{userId}/course/{courseId}/update")
    @Operation(summary = "Mettre à jour automatiquement la progression", description = "Calcule et met à jour automatiquement la progression d'un utilisateur pour un cours")
    public ResponseEntity<Progress> updateProgressAutomatically(@PathVariable Long userId, @PathVariable Long courseId) {
        try {
            Progress progress = progressService.updateProgressAutomatically(userId, courseId);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/user/{userId}/course/{courseId}/initialize")
    @Operation(summary = "Initialiser la progression", description = "Initialise la progression d'un utilisateur pour un cours")
    public ResponseEntity<Progress> initializeProgressForCourse(@PathVariable Long userId, @PathVariable Long courseId) {
        try {
            Progress progress = progressService.initializeProgressForCourse(userId, courseId);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/course/{courseId}/percentage")
    @Operation(summary = "Calculer le pourcentage de progression", description = "Retourne le pourcentage de progression d'un utilisateur pour un cours")
    public ResponseEntity<Double> calculateProgressPercentage(@PathVariable Long userId, @PathVariable Long courseId) {
        try {
            double percentage = progressService.calculateProgressPercentage(userId, courseId);
            return ResponseEntity.ok(percentage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/course/{courseId}/completed")
    @Operation(summary = "Vérifier si le cours est terminé", description = "Vérifie si un utilisateur a terminé un cours")
    public ResponseEntity<Boolean> isCourseCompleted(@PathVariable Long userId, @PathVariable Long courseId) {
        try {
            boolean completed = progressService.isCourseCompleted(userId, courseId);
            return ResponseEntity.ok(completed);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/course/{courseId}/current-lesson")
    @Operation(summary = "Obtenir la leçon courante", description = "Retourne l'ID de la leçon courante d'un utilisateur dans un cours")
    public ResponseEntity<Long> getCurrentLessonId(@PathVariable Long userId, @PathVariable Long courseId) {
        try {
            Long currentLessonId = progressService.getCurrentLessonId(userId, courseId);
            return ResponseEntity.ok(currentLessonId);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
