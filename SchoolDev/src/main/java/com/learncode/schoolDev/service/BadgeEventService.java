package com.learncode.schoolDev.service;

import com.learncode.schoolDev.event.BadgeEvent;
import com.learncode.schoolDev.model.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class BadgeEventService {

    private final ApplicationEventPublisher eventPublisher;

    public BadgeEventService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishExerciseCompleted(User user) {
        eventPublisher.publishEvent(new BadgeEvent(user, BadgeEvent.EXERCISE_COMPLETED));
    }


    public void publishLessonCompleted(User user) {
        eventPublisher.publishEvent(new BadgeEvent(user, BadgeEvent.LESSON_COMPLETED));
    }

    public void publishCourseFinished(User user) {
        eventPublisher.publishEvent(new BadgeEvent(user, BadgeEvent.COURSE_FINISHED));
    }

    public void publishUserRegistered(User user) {
        eventPublisher.publishEvent(new BadgeEvent(user, BadgeEvent.USER_REGISTERED));
    }

    public void publishCustomEvent(User user, String actionType, Object additionalData) {
        eventPublisher.publishEvent(new BadgeEvent(user, actionType, additionalData));
    }
}