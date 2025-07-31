package com.learncode.schoolDev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.learncode.schoolDev.dto.QcmPropositionRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exerciseId;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    
    @NotBlank(message = "La description est obligatoire")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private ExerciseType type = ExerciseType.CODE;
    
    // Champs pour les exercices de code (optionnels, validés selon le type)
    @Column(nullable = true)
    private String starterCode;
    
    @Column(nullable = true)
    private String testCases;
    
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    
    // Relation pour les propositions QCM
    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QcmProposition> qcmPropositions = new ArrayList<>();
    
    // Propriétés transientes pour simplifier l'API
    @Transient
    @JsonProperty("lessonId")
    private Long lessonIdForApi;
    
    @Transient
    @JsonProperty("propositions")
    private List<QcmPropositionRequest> propositionsForApi;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
    
    public ExerciseType getType() {
        return type;
    }
    
    public void setType(ExerciseType type) {
        this.type = type;
    }
    
    public List<QcmProposition> getQcmPropositions() {
        return qcmPropositions;
    }
    
    public void setQcmPropositions(List<QcmProposition> qcmPropositions) {
        this.qcmPropositions = qcmPropositions;
    }
    
    public void addQcmProposition(QcmProposition proposition) {
        qcmPropositions.add(proposition);
        proposition.setExercise(this);
    }
    
    public void removeQcmProposition(QcmProposition proposition) {
        qcmPropositions.remove(proposition);
        proposition.setExercise(null);
    }
    
    public boolean isCodeExercise() {
        return type == ExerciseType.CODE;
    }
    
    public boolean isQcmExercise() {
        return type == ExerciseType.QCM;
    }
    
    // Getters/Setters pour les propriétés transientes API
    public Long getLessonIdForApi() {
        return lessonIdForApi;
    }
    
    public void setLessonIdForApi(Long lessonIdForApi) {
        this.lessonIdForApi = lessonIdForApi;
    }
    
    public List<QcmPropositionRequest> getPropositionsForApi() {
        return propositionsForApi;
    }
    
    public void setPropositionsForApi(List<QcmPropositionRequest> propositionsForApi) {
        this.propositionsForApi = propositionsForApi;
    }
}