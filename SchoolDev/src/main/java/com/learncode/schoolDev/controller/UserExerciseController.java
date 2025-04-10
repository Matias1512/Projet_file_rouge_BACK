package com.learncode.schoolDev.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learncode.schoolDev.model.UserExercise;
import com.learncode.schoolDev.service.UserExerciseService;

@RestController
@RequestMapping("/api/user-exercises")
public class UserExerciseController {

    private final UserExerciseService service;

    public UserExerciseController(UserExerciseService service) {
        this.service = service;
    }

    @PostMapping
    public UserExercise create(@RequestBody UserExercise userExercise) {
        return service.save(userExercise);
    }

    @GetMapping
    public List<UserExercise> getAll() {
        return service.getAll();
    }

    @GetMapping("/user/{userId}")
    public List<UserExercise> getByUser(@PathVariable Long userId) {
        return service.getByUserId(userId);
    }

    @GetMapping("/exercise/{exerciseId}")
    public List<UserExercise> getByExercise(@PathVariable Long exerciseId) {
        return service.getByExerciseId(exerciseId);
    }
}