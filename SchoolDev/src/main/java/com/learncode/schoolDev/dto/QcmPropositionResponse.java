package com.learncode.schoolDev.dto;

public class QcmPropositionResponse {
    private Long propositionId;
    private String text;
    private boolean isCorrect;

    // Constructeurs
    public QcmPropositionResponse() {}

    public QcmPropositionResponse(Long propositionId, String text, boolean isCorrect) {
        this.propositionId = propositionId;
        this.text = text;
        this.isCorrect = isCorrect;
    }

    // Getters et Setters
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
}