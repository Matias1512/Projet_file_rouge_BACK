package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;

import com.learncode.schoolDev.model.Course;
import com.learncode.schoolDev.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course updatedCourse) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setTitle(updatedCourse.getTitle());
                    course.setLanguage(updatedCourse.getLanguage());
                    course.setDifficultyLevel(updatedCourse.getDifficultyLevel());
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new RuntimeException("Course non trouvé avec ID : " + id));
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}

