package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;

import com.learncode.schoolDev.dto.LessonCreateRequest;
import com.learncode.schoolDev.model.Course;
import com.learncode.schoolDev.model.Lesson;
import com.learncode.schoolDev.repository.CourseRepository;
import com.learncode.schoolDev.repository.LessonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    public LessonService(LessonRepository lessonRepository, CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public Optional<Lesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    public List<Lesson> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourse_CourseId(courseId);
    }

    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public Lesson createLessonFromRequest(LessonCreateRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new RuntimeException("Cours non trouvé avec ID : " + request.getCourseId()));
        
        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setOrderInCourse(request.getOrderInCourse());
        lesson.setCourse(course);
        
        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(Long id, Lesson updatedLesson) {
        return lessonRepository.findById(id)
                .map(lesson -> {
                    lesson.setTitle(updatedLesson.getTitle());
                    lesson.setContent(updatedLesson.getContent());
                    lesson.setOrderInCourse(updatedLesson.getOrderInCourse());
                    lesson.setCourse(updatedLesson.getCourse());
                    return lessonRepository.save(lesson);
                })
                .orElseThrow(() -> new RuntimeException("Leçon non trouvée avec ID : " + id));
    }

    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }
}
