package com.learncode.schoolDev.dto;

import com.learncode.schoolDev.model.ExerciseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ExerciseCreateRequest {
    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    
    @NotBlank(message = "La description est obligatoire")  
    private String description;
    
    @NotNull(message = "Le type d'exercice est obligatoire")
    private ExerciseType type;
    
    @NotNull(message = "L'ID de la leçon est obligatoire")
    private Long lessonId;
    
    // Champs pour exercices de code
    private String starterCode;
    private String testCases;
    
    // Champs pour exercices QCM (validation conditionnelle dans le service)
    @Valid
    private List<QcmPropositionDto> propositions;

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

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
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

    public List<QcmPropositionDto> getPropositions() {
        return propositions;
    }

    public void setPropositions(List<QcmPropositionDto> propositions) {
        this.propositions = propositions;
    }
}