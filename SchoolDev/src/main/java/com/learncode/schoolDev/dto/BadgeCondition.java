package com.learncode.schoolDev.dto;

import com.learncode.schoolDev.enums.BadgeConditionType;

public class BadgeCondition {
    private BadgeConditionType type;
    private int targetValue;
    private String language; // Pour les conditions spécifiques à un langage
    private String additionalData; // Pour des données supplémentaires si nécessaire

    public BadgeCondition() {}

    public BadgeCondition(BadgeConditionType type, int targetValue) {
        this.type = type;
        this.targetValue = targetValue;
    }

    public BadgeCondition(BadgeConditionType type, int targetValue, String language) {
        this.type = type;
        this.targetValue = targetValue;
        this.language = language;
    }

    // Getters et Setters
    public BadgeConditionType getType() {
        return type;
    }

    public void setType(BadgeConditionType type) {
        this.type = type;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    @Override
    public String toString() {
        return "BadgeCondition{" +
                "type=" + type +
                ", targetValue=" + targetValue +
                ", language='" + language + '\'' +
                ", additionalData='" + additionalData + '\'' +
                '}';
    }
}