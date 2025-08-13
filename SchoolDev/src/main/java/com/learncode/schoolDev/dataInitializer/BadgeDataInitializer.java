package com.learncode.schoolDev.dataInitializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.repository.BadgeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Order(1) // S'exécute en premier pour que les badges soient disponibles lors de l'inscription
public class BadgeDataInitializer implements CommandLineRunner {

    private final BadgeRepository badgeRepository;
    private final ObjectMapper objectMapper;

    public BadgeDataInitializer(BadgeRepository badgeRepository, ObjectMapper objectMapper) {
        this.badgeRepository = badgeRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (badgeRepository.count() == 0) {
            loadBadgesFromJson();
        }
    }

    private void loadBadgesFromJson() {
        try {
            ClassPathResource resource = new ClassPathResource("ListOfBadge.json");
            List<Map<String, Object>> badgeDataList = objectMapper.readValue(
                resource.getInputStream(), 
                new TypeReference<List<Map<String, Object>>>() {}
            );

            for (Map<String, Object> badgeData : badgeDataList) {
                if (!badgeRepository.existsByTitle((String) badgeData.get("name"))) {
                    Badge badge = new Badge();
                    badge.setTitle((String) badgeData.get("name"));
                    badge.setDescription((String) badgeData.get("description"));
                    badge.setIcon((String) badgeData.get("iconUrl"));
                    badge.setLevel(badgeData.get("level") != null ? (Integer) badgeData.get("level") : 1);
                    badge.setColor((String) badgeData.getOrDefault("color", "blue.500"));
                    badge.setTotal(badgeData.get("total") != null ? (Integer) badgeData.get("total") : 1);
                    badge.setUnlockRequirement((String) badgeData.get("unlockRequirement"));

                    badgeRepository.save(badge);
                    System.out.println("Badge créé: " + badge.getTitle());
                }
            }

            System.out.println("Initialisation des badges terminée. Total: " + badgeRepository.count() + " badges");

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des badges depuis le fichier JSON: " + e.getMessage());
        }
    }
}