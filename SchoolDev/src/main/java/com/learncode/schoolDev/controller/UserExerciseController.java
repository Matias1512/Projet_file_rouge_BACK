package com.learncode.schoolDev.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learncode.schoolDev.model.UserExercise;
import com.learncode.schoolDev.service.UserExerciseService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user-exercises")
@Tag(name = "UserExercice", description = "Gestion du liens entre les utilisateurs et les exercices")
public class UserExerciseController {

    private final UserExerciseService service;

    public UserExerciseController(UserExerciseService service) {
        this.service = service;
    }

    @PostMapping
    public UserExercise create(@Valid @RequestBody UserExercise userExercise) {
        return service.save(userExercise);
    }

    @PostMapping("/create")
    @Operation(summary = "Créer un UserExercise avec seulement userId et exerciseId")
    public UserExercise createWithIds(
            @RequestParam Long userId,
            @RequestParam Long exerciseId,
            @RequestParam Boolean success) {
        return service.createUserExercise(userId, exerciseId, success);
    }

    @GetMapping
    public List<UserExercise> getAll() {
        return service.getAll();
    }

    @GetMapping("/user/{userId}")
    public List<UserExercise> getByUser(@PathVariable Long userId) {
        return service.getByUserId(userId);
    }

    @GetMapping("/exercise/{exerciseId}")
    public List<UserExercise> getByExercise(@PathVariable Long exerciseId) {
        return service.getByExerciseId(exerciseId);
    }

    @GetMapping("/user/{userId}/exercise/{exerciseId}")
    @Operation(summary = "Trouver un UserExercise spécifique par userId et exerciseId")
    public ResponseEntity<UserExercise> getByUserAndExercise(
            @PathVariable Long userId,
            @PathVariable Long exerciseId) {
        Optional<UserExercise> userExercise = service.findByUserIdAndExerciseId(userId, exerciseId);
        return userExercise.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/success")
    @Operation(summary = "Mettre à jour le statut de réussite d'un UserExercise")
    public ResponseEntity<UserExercise> updateSuccess(
            @PathVariable Long id,
            @RequestParam Boolean success) {
        Optional<UserExercise> updated = service.updateSuccess(id, success);
        return updated.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
}