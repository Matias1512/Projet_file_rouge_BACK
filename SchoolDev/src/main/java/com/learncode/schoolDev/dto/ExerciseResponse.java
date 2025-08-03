package com.learncode.schoolDev.dto;

import com.learncode.schoolDev.model.ExerciseType;
import com.learncode.schoolDev.model.Lesson;
import java.time.LocalDateTime;
import java.util.List;

public class ExerciseResponse {
    private Long exerciseId;
    private String title;
    private String description;
    private ExerciseType type;
    private String starterCode;
    private String testCases;
    private LocalDateTime createdAt;
    private List<QcmPropositionResponse> propositions;
    private Long lessonId;
    private Lesson lesson;

    // Constructeurs
    public ExerciseResponse() {}

    public ExerciseResponse(Long exerciseId, String title, String description, ExerciseType type, 
                           String starterCode, String testCases, LocalDateTime createdAt) {
        this.exerciseId = exerciseId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.starterCode = starterCode;
        this.testCases = testCases;
        this.createdAt = createdAt;
    }

    // Getters et Setters
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

    public String getStarterCode() {
        return starterCode;
    }

    public void setStarterCode(String starterCode) {
        this.starterCode = starterCode;
    }

    public String getTestCases() {
        return testCases;
    }

    public void setTestCases(String testCases) {
        this.testCases = testCases;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<QcmPropositionResponse> getPropositions() {
        return propositions;
    }

    public void setPropositions(List<QcmPropositionResponse> propositions) {
        this.propositions = propositions;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
}