package com.learncode.schoolDev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LessonCreateRequest {
    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    
    @NotBlank(message = "Le contenu est obligatoire")
    private String content;
    
    @NotNull(message = "L'ordre dans le cours est obligatoire")
    private Integer orderInCourse;
    
    @NotNull(message = "L'ID du cours est obligatoire")
    private Long courseId;

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

    public Integer getOrderInCourse() {
        return orderInCourse;
    }

    public void setOrderInCourse(Integer orderInCourse) {
        this.orderInCourse = orderInCourse;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}