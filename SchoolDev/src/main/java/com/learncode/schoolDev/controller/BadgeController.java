package com.learncode.schoolDev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.service.BadgeService;
import com.learncode.schoolDev.service.BadgeEvaluationService;
import com.learncode.schoolDev.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/badges")
@Tag(name = "Badges", description = "Gestion des badges")
public class BadgeController {
    private final BadgeService badgeService;
    private final BadgeEvaluationService badgeEvaluationService;
    private final UserRepository userRepository;

    public BadgeController(BadgeService badgeService, BadgeEvaluationService badgeEvaluationService,
                         UserRepository userRepository) {
        this.badgeService = badgeService;
        this.badgeEvaluationService = badgeEvaluationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Liste tous les badges", description = "Retourne la liste complète des badges")
    public List<Badge> getAllBadges() {
        return badgeService.getAllBadges();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un badge par ID", description = "Retourne les détails d'un badge spécifique")
    public ResponseEntity<Badge> getBadgeById(@PathVariable Long id) {
        Optional<Badge> badge = badgeService.getBadgeById(id);
        return badge.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau badge", description = "Ajoute un nouveau badge à la base de données")
    public Badge createBadge(@Valid @RequestBody Badge request) {
        return badgeService.createBadge(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un badge", description = "Met à jour les informations d'un badge existant")
    public ResponseEntity<Optional<Badge>> updateBadge(@PathVariable Long id, @RequestBody Badge updatedBadge) {
        try {
            return ResponseEntity.ok(badgeService.updateBadge(id, updatedBadge));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un badge", description = "Supprime un badge de la base de données")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/evaluate/{userId}")
    @Operation(summary = "Évaluer les badges d'un utilisateur", description = "Force l'évaluation et l'attribution des badges pour un utilisateur")
    public ResponseEntity<List<Badge>> evaluateUserBadges(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Badge> newBadges = badgeEvaluationService.evaluateAndAssignBadges(userOpt.get());
        return ResponseEntity.ok(newBadges);
    }
}