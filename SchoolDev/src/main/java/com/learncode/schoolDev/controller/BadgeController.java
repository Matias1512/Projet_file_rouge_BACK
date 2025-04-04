package com.learncode.schoolDev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.service.BadgeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/badges")
@Tag(name = "Badges", description = "Gestion des badges")
public class BadgeController {
    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @GetMapping
    @Operation(summary = "Liste tous les badges de ta mere !!!", description = "Retourne la liste complète des badges")
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
    public Badge createBadge(@RequestBody Badge badge) {
        return badgeService.createBadge(badge);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un badge", description = "Met à jour les informations d'un badge existant")
    public ResponseEntity<Badge> updateBadge(@PathVariable Long id, @RequestBody Badge updatedBadge) {
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
}