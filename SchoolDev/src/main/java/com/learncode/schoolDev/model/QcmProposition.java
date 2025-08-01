package com.learncode.schoolDev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "qcm_propositions")
public class QcmProposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propositionId;

    @NotBlank(message = "Le texte de la proposition est obligatoire")
    private String text;

    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    @JsonBackReference
    private Exercise exercise;

    public Long getPropositionId() {
        return propositionId;
    }

    public void setPropositionId(Long propositionId) {
        this.propositionId = propositionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }
}