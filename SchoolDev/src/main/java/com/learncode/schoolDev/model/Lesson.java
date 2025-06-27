package com.learncode.schoolDev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "Lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonId;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    @NotBlank(message = "Le contenu est obligatoire")
    private String content;
    @NotNull(message = "L'ordre dans le cours est obligatoire")
    private Integer orderInCourse;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrderInCourse() {
        return orderInCourse;
    }

    public void setOrderInCourse(int orderInCourse) {
        this.orderInCourse = orderInCourse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
