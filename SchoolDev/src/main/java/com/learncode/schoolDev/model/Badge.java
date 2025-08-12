package com.learncode.schoolDev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "Badges")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    
    @NotBlank(message = "La description est obligatoire")
    private String description;
    
    @NotBlank(message = "L'icône est obligatoire")
    private String icon;
    
    @NotNull(message = "Le niveau est obligatoire")
    private Integer level;
    
    @NotBlank(message = "La couleur est obligatoire")
    private String color;
    
    @NotNull(message = "La valeur actuelle est obligatoire")
    private Integer current;
    
    @NotNull(message = "La valeur totale est obligatoire")
    private Integer total;
    
    @NotNull(message = "Le statut débloqué est obligatoire")
    private Boolean unlocked;
    
    private String unlockRequirement;
    
    private LocalDateTime createdAt;


    public Badge() {
        // Constructeur par défaut
    }

    // Constructeur simplifié pour compatibilité avec l'ancien format (4 paramètres)
    public Badge(Long id, String title, String description, String icon) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        // Valeurs par défaut
        this.level = 1;
        this.color = "blue.500";
        this.current = 0;
        this.total = 1;
        this.unlocked = true;
    }

    // Constructeur complet
    public Badge(Long id, String title, String description, String icon, Integer level, 
                 String color, Integer current, Integer total, Boolean unlocked) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.level = level;
        this.color = color;
        this.current = current;
        this.total = total;
        this.unlocked = unlocked;
    }
    
    // Constructeur complet avec unlock requirement
    public Badge(Long id, String title, String description, String icon, Integer level, 
                 String color, Integer current, Integer total, Boolean unlocked, String unlockRequirement) {
        this(id, title, description, icon, level, color, current, total, unlocked);
        this.unlockRequirement = unlockRequirement;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getUnlockRequirement() {
        return unlockRequirement;
    }

    public void setUnlockRequirement(String unlockRequirement) {
        this.unlockRequirement = unlockRequirement;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}