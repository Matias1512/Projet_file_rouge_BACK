package com.learncode.schoolDev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(name = "title")
    private String title;
    
    @NotBlank(message = "La description est obligatoire")
    @Column(name = "description")
    private String description;
    
    @NotBlank(message = "L'icône est obligatoire")
    @Column(name = "icon_url")
    private String icon;
    
    @Column(name = "icon")
    private String iconName;
    
    @NotNull(message = "Le niveau est obligatoire")
    @Column(name = "level")
    private Integer level;
    
    @NotBlank(message = "La couleur est obligatoire")
    @Column(name = "color")
    private String color;
    
    @NotNull(message = "La valeur totale est obligatoire")
    @Column(name = "total")
    private Integer total;
    
    @Column(name = "unlock_requirement")
    private String unlockRequirement;
    
    @Column(name = "created_at")
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
        this.total = 1;
    }

    // Constructeur complet
    public Badge(Long id, String title, String description, String icon, Integer level, 
                 String color, Integer total) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.level = level;
        this.color = color;
        this.total = total;
    }
    
    // Constructeur complet avec unlock requirement
    public Badge(Long id, String title, String description, String icon, Integer level, 
                 String color, Integer total, String unlockRequirement) {
        this(id, title, description, icon, level, color, total);
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

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
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

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
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