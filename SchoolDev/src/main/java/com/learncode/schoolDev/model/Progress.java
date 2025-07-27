package com.learncode.schoolDev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "Progress")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId;

    @NotNull(message = "L'ID de la leçon est obligatoire")
    private Long currentLessonId;
    @NotNull(message = "Le pourcentage de progression est obligatoire")
    private double percentageCompleted;
    private LocalDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getProgressId() {
        return progressId;
    }

    public void setProgressId(Long progressId) {
        this.progressId = progressId;
    }

    public Long getCurrentLessonId() {
        return currentLessonId;
    }

    public void setCurrentLessonId(Long currentLessonId) {
        this.currentLessonId = currentLessonId;
    }

    public double getPercentageCompleted() {
        return percentageCompleted;
    }

    public void setPercentageCompleted(double percentageCompleted) {
        this.percentageCompleted = percentageCompleted;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
