package com.learncode.schoolDev.event;

import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.service.BadgeEvaluationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BadgeEventListener {

    private final BadgeEvaluationService badgeEvaluationService;

    public BadgeEventListener(BadgeEvaluationService badgeEvaluationService) {
        this.badgeEvaluationService = badgeEvaluationService;
    }

    @EventListener
    @Async
    public void handleBadgeEvent(BadgeEvent event) {
        try {
            // Évaluer et attribuer les badges pour l'utilisateur
            List<Badge> newBadges = badgeEvaluationService.evaluateBadgesAfterAction(
                event.getUser(), 
                event.getActionType()
            );

            // Log des nouveaux badges obtenus
            if (!newBadges.isEmpty()) {
                System.out.println("Utilisateur " + event.getUser().getUsername() + 
                                 " a obtenu " + newBadges.size() + " nouveau(x) badge(s):");
                for (Badge badge : newBadges) {
                    System.out.println("- " + badge.getTitle());
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'évaluation des badges pour l'utilisateur " + 
                             event.getUser().getUsername() + ": " + e.getMessage());
        }
    }
}