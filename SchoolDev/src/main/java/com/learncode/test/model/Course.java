package com.learncode.schooldev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import com.learncode.schooldev.enums.DifficultyLevel;

@Entity
@Table(name = "Courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    @NotBlank(message = "La langue est obligatoire")
    private String language;
    private LocalDateTime createdAt;
    
    @NotNull(message = "Le niveau de difficulté est obligatoire")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    public Course(long courseId, String title, String language, String difficultyLevel) {
        this.courseId = courseId;
        this.title = title;
        this.language = language;
        this.difficultyLevel = DifficultyLevel.valueOf(difficultyLevel);
        this.createdAt = LocalDateTime.now();
    }

    public Course() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    
}
