package com.learncode.schoolDev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Requête pour créer une association utilisateur-badge")
public class UserBadgeCreateRequest {

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    @Positive(message = "L'ID de l'utilisateur doit être positif")
    @Schema(description = "ID de l'utilisateur", example = "1")
    private Long userId;

    @NotNull(message = "L'ID du badge est obligatoire")
    @Positive(message = "L'ID du badge doit être positif")
    @Schema(description = "ID du badge", example = "1")
    private Long badgeId;

    @Schema(description = "Valeur actuelle pour le badge (optionnel, par défaut 0)", example = "0")
    private Integer current = 0;

    @Schema(description = "Statut débloqué (optionnel, par défaut false)", example = "false")
    private Boolean unlocked = false;

    public UserBadgeCreateRequest() {}

    public UserBadgeCreateRequest(Long userId, Long badgeId) {
        this.userId = userId;
        this.badgeId = badgeId;
    }

    public UserBadgeCreateRequest(Long userId, Long badgeId, Integer current, Boolean unlocked) {
        this.userId = userId;
        this.badgeId = badgeId;
        this.current = current;
        this.unlocked = unlocked;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(Long badgeId) {
        this.badgeId = badgeId;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
    }
}