package com.learncode.schoolDev.enums;

public enum BadgeConditionType {
    EXERCISES_COMPLETED("exercises_completed"),
    COURSES_FINISHED("courses_finished"), 
    STREAK_DAYS("streak_days"),
    LESSONS_COMPLETED("lessons_completed"),
    LANGUAGE_EXERCISES("language_exercises"),
    TOTAL_XP("total_xp");

    private final String value;

    BadgeConditionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BadgeConditionType fromValue(String value) {
        for (BadgeConditionType type : BadgeConditionType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown badge condition type: " + value);
    }
}