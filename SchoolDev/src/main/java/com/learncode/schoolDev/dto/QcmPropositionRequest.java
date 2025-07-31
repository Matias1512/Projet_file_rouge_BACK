package com.learncode.schoolDev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QcmPropositionRequest {
    @NotBlank(message = "Le texte de la proposition est obligatoire")
    private String text;
    
    @NotNull(message = "Il faut indiquer si la proposition est correcte")
    private Boolean isCorrect;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}