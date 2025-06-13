package com.learncode.schooldev.controller;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learncode.schooldev.model.Lesson;
import com.learncode.schooldev.service.LessonService;

@RestController
@RequestMapping("/api/lessons")
@Tag(name = "Lessons", description = "Gestion des leçons")
public class LessonController {
    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping
    @Operation(summary = "Liste toutes les leçons", description = "Retourne la liste complète des leçons")
    public List<Lesson> getAllLessons() {
        return lessonService.getAllLessons();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une leçon par ID", description = "Retourne les détails d'une leçon spécifique")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long id) {
        Optional<Lesson> lesson = lessonService.getLessonById(id);
        return lesson.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Obtenir les leçons d'un cours", description = "Retourne la liste des leçons d'un cours spécifique")
    public List<Lesson> getLessonsByCourse(@PathVariable Long courseId) {
        return lessonService.getLessonsByCourse(courseId);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle leçon", description = "Ajoute une nouvelle leçon à la base de données")
    public Lesson createLesson(@Valid @RequestBody Lesson lesson) {
        return lessonService.createLesson(lesson);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une leçon", description = "Met à jour les informations d'une leçon existante")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id, @RequestBody Lesson updatedLesson) {
        try {
            return ResponseEntity.ok(lessonService.updateLesson(id, updatedLesson));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une leçon", description = "Supprime une leçon de la base de données")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}

