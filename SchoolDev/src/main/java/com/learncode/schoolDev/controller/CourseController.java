package com.learncode.schoolDev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learncode.schoolDev.model.Course;
import com.learncode.schoolDev.service.CourseService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Gestion des cours")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "Liste tous les cours", description = "Retourne la liste complète des cours")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un cours par ID", description = "Retourne les détails d'un cours spécifique")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau cours", description = "Ajoute un nouveau cours à la base de données")
    public Course createCourse(@Valid @RequestBody Course request) {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setLanguage(request.getLanguage());
        course.setDifficultyLevel(request.getDifficultyLevel()); // Assurez-vous que le niveau de difficulté est un enum ou une chaîne valide
        return courseService.createCourse(course);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un cours", description = "Met à jour les informations d'un cours existant")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        try {
            return ResponseEntity.ok(courseService.updateCourse(id, updatedCourse));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un cours", description = "Supprime un cours de la base de données")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
