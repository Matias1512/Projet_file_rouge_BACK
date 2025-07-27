package com.learncode.schoolDev.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class JwtSecretReader {

    private static final Logger logger = LoggerFactory.getLogger(JwtSecretReader.class);

    @Value("${app.jwt.secret-key:your-256-bit-secret-key-change-in-production}")
    private String jwtSecretKey;

    public String getJwtSecretKey() {
        // Essayer de lire depuis Docker secret
        Path secretPath = Paths.get("/run/secrets/jwt_secret_key");
        if (Files.exists(secretPath)) {
            try {
                return Files.readString(secretPath).trim();
            } catch (IOException e) {
                logger.warn("Erreur lecture secret JWT: {}", e.getMessage());
            }
        }
        
        // Fallback sur la variable d'environnement ou valeur par défaut
        return jwtSecretKey;
    }
}