package com.learncode.schoolDev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.service.UserBadgeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/userbadges")
@Tag(name = "UserBadges", description = "Gestion des badges des utilisateurs")
public class UserBadgeController {
    private final UserBadgeService userBadgeService;

    public UserBadgeController(UserBadgeService userBadgeService) {
        this.userBadgeService = userBadgeService;
    }

    @GetMapping
    @Operation(summary = "Liste tous les badges des utilisateurs", description = "Retourne la liste complète des badges des utilisateurs")
    public List<UserBadge> getAllUserBadges() {
        return userBadgeService.getAllUserBadges();
    }

    @GetMapping("/get-by-composite-id")
    @Operation(summary = "Obtenir un badge utilisateur par ID", description = "Retourne les détails d'un badge utilisateur spécifique")
    public ResponseEntity<UserBadge> getUserBadgeById(@RequestParam Long userId, @RequestParam Long badgeId) {
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(userId, badgeId);
        Optional<UserBadge> userBadge = userBadgeService.getUserBadgeById(key);
        return userBadge.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtenir les badges d'un utilisateur", description = "Retourne la liste des badges d'un utilisateur spécifique")
    public List<UserBadge> getUserBadgesByUser(@PathVariable Long userId) {
        return userBadgeService.getUserBadgesByUser(userId);
    }

    @GetMapping("/badge/{badgeId}")
    @Operation(summary = "Obtenir les utilisateurs ayant un badge", description = "Retourne la liste des utilisateurs ayant un badge spécifique")
    public List<UserBadge> getUserBadgesByBadge(@PathVariable Long badgeId) {
        return userBadgeService.getUserBadgesByBadge(badgeId);
    }

    @PostMapping
    @Operation(summary = "Attribuer un badge à un utilisateur", description = "Ajoute une nouvelle association utilisateur-badge à la base de données")
    public UserBadge createUserBadge(@RequestBody UserBadge userBadge) {
        return userBadgeService.createUserBadge(userBadge);
    }

    @PutMapping("/update")
    @Operation(summary = "Mettre à jour un badge utilisateur", description = "Met à jour les informations d'un badge utilisateur existant")
    public ResponseEntity<UserBadge> updateUserBadge(@RequestParam Long userId, @RequestParam Long badgeId, @RequestBody UserBadge updatedUserBadge) {
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(userId, badgeId);
        try {
            return ResponseEntity.ok(userBadgeService.updateUserBadge(key, updatedUserBadge));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Supprimer un badge utilisateur", description = "Supprime un badge utilisateur de la base de données")
    public ResponseEntity<Void> deleteUserBadge(@RequestParam Long userId, @RequestParam Long badgeId) {
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(userId, badgeId);
        userBadgeService.deleteUserBadge(key);
        return ResponseEntity.noContent().build();
    }
}
