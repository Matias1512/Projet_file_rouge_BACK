package com.learncode.schoolDev.dto;

import com.learncode.schoolDev.model.ExerciseType;
import java.time.LocalDateTime;

public class ExerciseListResponse {
    private Long exerciseId;
    private String title;
    private String description;
    private ExerciseType type;
    private LocalDateTime createdAt;

    public ExerciseListResponse() {}

    public ExerciseListResponse(Long exerciseId, String title, String description, ExerciseType type, LocalDateTime createdAt) {
        this.exerciseId = exerciseId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
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

    public ExerciseType getType() {
        return type;
    }

    public void setType(ExerciseType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}