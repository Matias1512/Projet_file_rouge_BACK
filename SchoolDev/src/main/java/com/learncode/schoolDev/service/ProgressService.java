package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learncode.schoolDev.model.Course;
import com.learncode.schoolDev.model.Exercise;
import com.learncode.schoolDev.model.Lesson;
import com.learncode.schoolDev.model.Progress;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.UserExercise;
import com.learncode.schoolDev.repository.CourseRepository;
import com.learncode.schoolDev.repository.ExerciseRepository;
import com.learncode.schoolDev.repository.LessonRepository;
import com.learncode.schoolDev.repository.ProgressRepository;
import com.learncode.schoolDev.repository.UserExerciseRepository;
import com.learncode.schoolDev.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProgressService {
    private final ProgressRepository progressRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public ProgressService(ProgressRepository progressRepository, 
                          UserExerciseRepository userExerciseRepository,
                          ExerciseRepository exerciseRepository,
                          LessonRepository lessonRepository,
                          CourseRepository courseRepository,
                          UserRepository userRepository) {
        this.progressRepository = progressRepository;
        this.userExerciseRepository = userExerciseRepository;
        this.exerciseRepository = exerciseRepository;
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public List<Progress> getAllProgress() {
        return progressRepository.findAll();
    }

    public Optional<Progress> getProgressById(Long id) {
        return progressRepository.findById(id);
    }

    public List<Progress> getProgressByUser(Long userId) {
        return progressRepository.findByUser_UserId(userId);
    }

    public List<Progress> getProgressByCourse(Long courseId) {
        return progressRepository.findByCourse_CourseId(courseId);
    }

    public Optional<Progress> getProgressByUserAndCourse(Long userId, Long courseId) {
        return progressRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId);
    }

    public Progress createProgress(Progress progress) {
        return progressRepository.save(progress);
    }

    public Progress updateProgress(Long id, Progress updatedProgress) {
        return progressRepository.findById(id)
                .map(progress -> {
                    progress.setCurrentLessonId(updatedProgress.getCurrentLessonId());
                    progress.setPercentageCompleted(updatedProgress.getPercentageCompleted());
                    progress.setUser(updatedProgress.getUser());
                    progress.setCourse(updatedProgress.getCourse());
                    return progressRepository.save(progress);
                })
                .orElseThrow(() -> new RuntimeException("Progress non trouvé avec ID : " + id));
    }

    public void deleteProgress(Long id) {
        progressRepository.deleteById(id);
    }

    /**
     * Calcule automatiquement le pourcentage de progression d'un utilisateur pour un cours
     * basé sur les exercices réussis
     */
    public double calculateProgressPercentage(Long userId, Long courseId) {
        long totalExercises = exerciseRepository.countByCourseId(courseId);
        if (totalExercises == 0) {
            return 0.0;
        }
        
        long completedExercises = userExerciseRepository.countCompletedExercisesByUserAndCourse(userId, courseId);
        return (double) completedExercises / totalExercises * 100.0;
    }

    /**
     * Détermine la leçon courante d'un utilisateur dans un cours
     * (la première leçon avec des exercices non complétés)
     */
    public Long getCurrentLessonId(Long userId, Long courseId) {
        List<Lesson> lessons = lessonRepository.findByCourse_CourseIdOrderByOrderInCourse(courseId);
        
        for (Lesson lesson : lessons) {
            List<Exercise> exercises = exerciseRepository.findByLesson_LessonId(lesson.getLessonId());
            
            for (Exercise exercise : exercises) {
                List<UserExercise> successfulUserExercises = userExerciseRepository
                    .findSuccessfulUserExercisesByUserAndExercise(userId, exercise.getExerciseId());
                
                if (successfulUserExercises.isEmpty()) {
                    return lesson.getLessonId();
                }
            }
        }
        
        // Si tous les exercices sont complétés, retourner la dernière leçon
        return lessons.isEmpty() ? null : lessons.get(lessons.size() - 1).getLessonId();
    }

    /**
     * Met à jour automatiquement la progression d'un utilisateur pour un cours
     */
    public Progress updateProgressAutomatically(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID : " + userId));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Cours non trouvé avec ID : " + courseId));

        Optional<Progress> existingProgress = getProgressByUserAndCourse(userId, courseId);
        
        double percentage = calculateProgressPercentage(userId, courseId);
        Long currentLessonId = getCurrentLessonId(userId, courseId);
        
        if (existingProgress.isPresent()) {
            Progress progress = existingProgress.get();
            progress.setPercentageCompleted(percentage);
            progress.setCurrentLessonId(currentLessonId);
            return progressRepository.save(progress);
        } else {
            Progress newProgress = new Progress();
            newProgress.setUser(user);
            newProgress.setCourse(course);
            newProgress.setPercentageCompleted(percentage);
            newProgress.setCurrentLessonId(currentLessonId);
            return progressRepository.save(newProgress);
        }
    }


    /**
     * Initialise la progression pour un utilisateur qui commence un cours
     */
    public Progress initializeProgressForCourse(Long userId, Long courseId) {
        Optional<Progress> existingProgress = getProgressByUserAndCourse(userId, courseId);
        
        if (existingProgress.isPresent()) {
            return existingProgress.get();
        }
        
        return updateProgressAutomatically(userId, courseId);
    }

    /**
     * Vérifie si un utilisateur a terminé un cours (100% de progression)
     */
    public boolean isCourseCompleted(Long userId, Long courseId) {
        double percentage = calculateProgressPercentage(userId, courseId);
        return percentage >= 100.0;
    }
}
