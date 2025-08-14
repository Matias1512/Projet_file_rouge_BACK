package com.learncode.schoolDev.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learncode.schoolDev.config.JwtUtils;
import com.learncode.schoolDev.dto.LoginRequest;
import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.repository.BadgeRepository;
import com.learncode.schoolDev.repository.UserBadgeRepository;
import com.learncode.schoolDev.repository.UserRepository;
import com.learncode.schoolDev.service.BadgeEventService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Gestion de l'authentification des utilisateurs")

public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final BadgeEventService badgeEventService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, 
                         AuthenticationManager authenticationManager, UserBadgeRepository userBadgeRepository, 
                         BadgeRepository badgeRepository, BadgeEventService badgeEventService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
        this.badgeEventService = badgeEventService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
            //System.out.println("Username already exists");
        } else {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            User savedUser = userRepository.save(user);
            
            try {
                List<Badge> allBadges = badgeRepository.findAll();

                // Créer les UserBadge seulement si des badges existent
                if (!allBadges.isEmpty()) {
                    List<UserBadge> userBadges = new ArrayList<>();
                    for (Badge badge : allBadges) {
                        UserBadge userBadge = new UserBadge();
                        userBadge.setUser(savedUser);
                        userBadge.setBadge(badge);
                        userBadge.setUnlocked(false); // Badges non débloqués par défaut
                        userBadge.setCurrent(0); // Progression à zéro
                        userBadges.add(userBadge);
                    }

                    userBadgeRepository.saveAll(userBadges);
                    
                    // Déclencher l'évaluation des badges seulement si des badges existent
                    badgeEventService.publishUserRegistered(savedUser);
                } else {
                    System.out.println("Aucun badge trouvé en base - création d'utilisateur sans badges");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la création des badges pour l'utilisateur: " + e.getMessage());
                // L'inscription continue même si les badges échouent
            }

            return ResponseEntity.ok(savedUser);
        } 
    }
    
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            ); 
            if(authentication.isAuthenticated()) {
                User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
                Map<String, Object> authData =  new HashMap<>();
                authData.put("token", jwtUtils.generateToken(user));
                authData.put("type", "Bearer");
                return ResponseEntity.ok(authData);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
