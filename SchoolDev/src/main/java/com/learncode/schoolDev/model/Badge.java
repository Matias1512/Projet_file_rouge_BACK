package com.learncode.schoolDev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "Badges")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long badgeId;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;
    @NotBlank(message = "La description est obligatoire")
    private String description;
    @NotBlank(message = "L'icône est obligatoire")
    private String iconUrl;
    private LocalDateTime createdAt;


    public Badge() {
        // Constructeur par défaut
    }

    public Badge(Long id, String name, String description, String iconUrl) {
        this.badgeId = id;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(Long badgeId) {
        this.badgeId = badgeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}