package com.learncode.schoolDev.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learncode.schoolDev.config.JwtUtils;
import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.repository.BadgeRepository;
import com.learncode.schoolDev.repository.UserBadgeRepository;
import com.learncode.schoolDev.repository.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Gestion de l'authentification des utilisateurs")

public class AuthController {

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        } else {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            User savedUser = userRepository.save(user);
            List<Badge> allBadges = badgeRepository.findAll();

            List<UserBadge> userBadges = new ArrayList<>();
            for (Badge badge : allBadges) {
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(savedUser);
                userBadge.setBadge(badge);
                userBadges.add(userBadge);
            }

            userBadgeRepository.saveAll(userBadges);

            return ResponseEntity.ok(savedUser);
        } 
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password"); // ⚠️ le champ "password" ici
        try {
            System.out.println("Registering user: " + username);
            System.out.println("Passport user: " + password);
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            ); 
            if(authentication.isAuthenticated()) {
                Map<String, Object> authData =  new HashMap<>();
                authData.put("token", jwtUtils.generateToken(username));
                authData.put("type", "Bearer");
                return ResponseEntity.ok(authData);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password 1");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password 2");
        }
    }
}
