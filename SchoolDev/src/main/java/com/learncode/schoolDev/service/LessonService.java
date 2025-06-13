package com.learncode.schooldev.service;

import org.springframework.stereotype.Service;

import com.learncode.schooldev.model.Lesson;
import com.learncode.schooldev.repository.LessonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
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
