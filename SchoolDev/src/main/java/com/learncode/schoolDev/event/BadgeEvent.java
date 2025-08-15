package com.learncode.schoolDev.event;

import com.learncode.schoolDev.model.User;

public class BadgeEvent {
    private final User user;
    private final String actionType;
    private final Object additionalData;

    public BadgeEvent(User user, String actionType) {
        this.user = user;
        this.actionType = actionType;
        this.additionalData = null;
    }

    public BadgeEvent(User user, String actionType, Object additionalData) {
        this.user = user;
        this.actionType = actionType;
        this.additionalData = additionalData;
    }

    public User getUser() {
        return user;
    }

    public String getActionType() {
        return actionType;
    }

    public Object getAdditionalData() {
        return additionalData;
    }

    // Types d'actions constants
    public static final String EXERCISE_COMPLETED = "exercise_completed";
    public static final String LESSON_COMPLETED = "lesson_completed";
    public static final String COURSE_FINISHED = "course_finished";
    public static final String USER_REGISTERED = "user_registered";
}