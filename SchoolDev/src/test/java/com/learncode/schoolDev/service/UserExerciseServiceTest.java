package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.Exercise;
import com.learncode.schoolDev.model.Lesson;
import com.learncode.schoolDev.model.Course;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.UserExercise;
import com.learncode.schoolDev.repository.UserExerciseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserExerciseServiceTest {

    @Mock
    private UserExerciseRepository repository;

    @InjectMocks
    private UserExerciseService service;

    private User user;
    private Exercise exercise;
    private Lesson lesson;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        course = new Course();
        course.setLanguage("Java");
        lesson = new Lesson();
        lesson.setCourse(course);
        exercise = new Exercise();
        exercise.setLesson(lesson);
        exercise.setExerciseId(2L);
    }

    @Test
    void save_ReturnsSaved() {
        UserExercise userExercise = new UserExercise();
        when(repository.save(userExercise)).thenReturn(userExercise);

        UserExercise result = service.save(userExercise);

        assertSame(userExercise, result);
        verify(repository).save(userExercise);
    }

    @Test
    void getAll_ReturnsList() {
        List<UserExercise> list = Arrays.asList(new UserExercise(), new UserExercise());
        when(repository.findAll()).thenReturn(list);

        List<UserExercise> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void getByUserId_ReturnsList() {
        when(repository.findByUser_UserId(1L)).thenReturn(Arrays.asList(new UserExercise()));

        List<UserExercise> result = service.getByUserId(1L);

        assertEquals(1, result.size());
        verify(repository).findByUser_UserId(1L);
    }

    @Test
    void getByExerciseId_ReturnsList() {
        when(repository.findByExercise_ExerciseId(2L)).thenReturn(Arrays.asList(new UserExercise()));

        List<UserExercise> result = service.getByExerciseId(2L);

        assertEquals(1, result.size());
        verify(repository).findByExercise_ExerciseId(2L);
    }

    @Test
    void getAllSuccessfulExercice_ReturnsOnlySuccess() {
        UserExercise ue1 = new UserExercise(); ue1.setSuccess(true);
        UserExercise ue2 = new UserExercise(); ue2.setSuccess(false);
        when(repository.findByUser_UserId(1L)).thenReturn(Arrays.asList(ue1, ue2));

        List<UserExercise> result = service.getAllSuccessfulExercice(1L);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getSuccess());
    }

    @Test
    void getSuccessfulExercicebyLanguage_ReturnsOnlySuccessAndLanguage() {
        Course javaCourse = new Course(); javaCourse.setLanguage("Java");
        Lesson lessonJava = new Lesson(); lessonJava.setCourse(javaCourse);
        Exercise exerciseJava = new Exercise(); exerciseJava.setLesson(lessonJava);

        UserExercise ueJavaSuccess = new UserExercise();
        ueJavaSuccess.setSuccess(true);
        ueJavaSuccess.setExercise(exerciseJava);

        Course pythonCourse = new Course(); pythonCourse.setLanguage("Python");
        Lesson lessonPython = new Lesson(); lessonPython.setCourse(pythonCourse);
        Exercise exercisePython = new Exercise(); exercisePython.setLesson(lessonPython);

        UserExercise uePythonSuccess = new UserExercise();
        uePythonSuccess.setSuccess(true);
        uePythonSuccess.setExercise(exercisePython);

        UserExercise ueJavaFail = new UserExercise();
        ueJavaFail.setSuccess(false);
        ueJavaFail.setExercise(exerciseJava);

        when(repository.findByUser_UserId(1L)).thenReturn(Arrays.asList(ueJavaSuccess, uePythonSuccess, ueJavaFail));

        List<UserExercise> result = service.getSuccessfulExercicebyLanguage(1L, "Java");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getSuccess());
        assertEquals("Java", result.get(0).getExercise().getLesson().getCourse().getLanguage());
    }
}
