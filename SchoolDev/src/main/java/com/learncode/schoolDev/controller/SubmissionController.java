package com.learncode.schoolDev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learncode.schoolDev.model.Submission;
import com.learncode.schoolDev.service.SubmissionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/submissions")
@Tag(name = "Submissions", description = "Gestion des soumissions")
public class SubmissionController {
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping
    @Operation(summary = "Liste toutes les soumissions", description = "Retourne la liste complète des soumissions")
    public List<Submission> getAllSubmissions() {
        return submissionService.getAllSubmissions();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une soumission par ID", description = "Retourne les détails d'une soumission spécifique")
    public ResponseEntity<Submission> getSubmissionById(@PathVariable Long id) {
        Optional<Submission> submission = submissionService.getSubmissionById(id);
        return submission.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtenir les soumissions d'un utilisateur", description = "Retourne la liste des soumissions d'un utilisateur spécifique")
    public List<Submission> getSubmissionsByUser(@PathVariable Long userId) {
        return submissionService.getSubmissionsByUser(userId);
    }

    @GetMapping("/exercise/{exerciseId}")
    @Operation(summary = "Obtenir les soumissions d'un exercice", description = "Retourne la liste des soumissions d'un exercice spécifique")
    public List<Submission> getSubmissionsByExercise(@PathVariable Long exerciseId) {
        return submissionService.getSubmissionsByExercise(exerciseId);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle soumission", description = "Ajoute une nouvelle soumission à la base de données")
    public Submission createSubmission(@RequestBody Submission submission) {
        return submissionService.createSubmission(submission);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une soumission", description = "Met à jour les informations d'une soumission existante")
    public ResponseEntity<Submission> updateSubmission(@PathVariable Long id, @RequestBody Submission updatedSubmission) {
        try {
            return ResponseEntity.ok(submissionService.updateSubmission(id, updatedSubmission));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une soumission", description = "Supprime une soumission de la base de données")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }
}
