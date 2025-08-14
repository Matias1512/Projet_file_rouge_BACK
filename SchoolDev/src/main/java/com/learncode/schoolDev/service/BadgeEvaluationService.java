package com.learncode.schoolDev.service;

import com.learncode.schoolDev.dto.BadgeCondition;
import com.learncode.schoolDev.enums.BadgeConditionType;
import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.repository.BadgeRepository;
import com.learncode.schoolDev.repository.UserBadgeRepository;
import com.learncode.schoolDev.repository.UserExerciseRepository;
import com.learncode.schoolDev.repository.SubmissionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BadgeEvaluationService {
    
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserExerciseRepository userExerciseRepository;
    private final SubmissionRepository submissionRepository;

    public BadgeEvaluationService(BadgeRepository badgeRepository, 
                                UserBadgeRepository userBadgeRepository,
                                UserExerciseRepository userExerciseRepository,
                                SubmissionRepository submissionRepository) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.userExerciseRepository = userExerciseRepository;
        this.submissionRepository = submissionRepository;
    }

    /**
     * Évalue et attribue tous les badges applicables pour un utilisateur
     */
    public List<Badge> evaluateAndAssignBadges(User user) {
        List<Badge> newlyEarnedBadges = new ArrayList<>();
        List<Badge> allBadges = badgeRepository.findAll();

        for (Badge badge : allBadges) {
            if (!userHasBadge(user, badge) && evaluateBadgeCondition(user, badge)) {
                assignBadgeToUser(user, badge);
                newlyEarnedBadges.add(badge);
            } else {
                // Mettre à jour la progression même si le badge n'est pas encore obtenu
                updateBadgeProgress(user, badge);
            }
        }

        return newlyEarnedBadges;
    }

    /**
     * Évalue une condition spécifique d'un badge pour un utilisateur
     */
    public boolean evaluateBadgeCondition(User user, Badge badge) {
        if (badge.getUnlockRequirement() == null || badge.getUnlockRequirement().trim().isEmpty()) {
            return false; // Pas de condition définie
        }

        BadgeCondition condition = parseCondition(badge.getUnlockRequirement());
        if (condition == null) {
            return false;
        }

        return evaluateCondition(user, condition);
    }

    /**
     * Parse une condition depuis un string
     * Format attendu: "type:value" ou "type:value:language"
     * Exemples: "exercises_completed:10", "language_exercises:5:JAVA"
     */
    private BadgeCondition parseCondition(String conditionString) {
        try {
            String[] parts = conditionString.split(":");
            if (parts.length < 2) {
                return null;
            }

            BadgeConditionType type = BadgeConditionType.fromValue(parts[0]);
            int targetValue = Integer.parseInt(parts[1]);
            
            BadgeCondition condition = new BadgeCondition(type, targetValue);
            
            if (parts.length > 2) {
                condition.setLanguage(parts[2]);
            }
            
            return condition;
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing de la condition: " + conditionString + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Évalue une condition spécifique
     */
    private boolean evaluateCondition(User user, BadgeCondition condition) {
        switch (condition.getType()) {
            case EXERCISES_COMPLETED:
                return countCompletedExercises(user) >= condition.getTargetValue();
            
            case SUBMISSION_SUCCESS:
                return countSuccessfulSubmissions(user) >= condition.getTargetValue();
            
            case LANGUAGE_EXERCISES:
                if (condition.getLanguage() != null) {
                    return countLanguageExercises(user, condition.getLanguage()) >= condition.getTargetValue();
                }
                return false;
            
            case LESSONS_COMPLETED:
                return countCompletedLessons(user) >= condition.getTargetValue();
            
            case PERFECT_SCORE:
                return countPerfectScores(user) >= condition.getTargetValue();
            
            default:
                return false;
        }
    }

    /**
     * Met à jour la progression d'un badge pour un utilisateur
     */
    public void updateBadgeProgress(User user, Badge badge) {
        Optional<UserBadge> userBadgeOpt = userBadgeRepository.findByUser_UserIdAndBadge_Id(user.getUserId(), badge.getId());
        
        if (userBadgeOpt.isPresent()) {
            UserBadge userBadge = userBadgeOpt.get();
            
            // Calculer la progression actuelle
            int currentProgress = calculateCurrentProgress(user, badge);
            userBadge.setCurrent(currentProgress);
            
            // Vérifier si le badge doit être débloqué
            if (!userBadge.getUnlocked() && evaluateBadgeCondition(user, badge)) {
                userBadge.setUnlocked(true);
                userBadge.setUnlockedAt(LocalDateTime.now());
            }
            
            userBadgeRepository.save(userBadge);
        }
    }

    /**
     * Calcule la progression actuelle pour un badge
     */
    private int calculateCurrentProgress(User user, Badge badge) {
        if (badge.getUnlockRequirement() == null) {
            return 0;
        }

        BadgeCondition condition = parseCondition(badge.getUnlockRequirement());
        if (condition == null) {
            return 0;
        }

        switch (condition.getType()) {
            case EXERCISES_COMPLETED:
                return Math.min(countCompletedExercises(user), condition.getTargetValue());
            
            case SUBMISSION_SUCCESS:
                return Math.min(countSuccessfulSubmissions(user), condition.getTargetValue());
            
            case LANGUAGE_EXERCISES:
                if (condition.getLanguage() != null) {
                    return Math.min(countLanguageExercises(user, condition.getLanguage()), condition.getTargetValue());
                }
                return 0;
            
            case LESSONS_COMPLETED:
                return Math.min(countCompletedLessons(user), condition.getTargetValue());
            
            case PERFECT_SCORE:
                return Math.min(countPerfectScores(user), condition.getTargetValue());
            
            default:
                return 0;
        }
    }

    /**
     * Assigne un badge à un utilisateur
     */
    private void assignBadgeToUser(User user, Badge badge) {
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setUnlocked(true);
        userBadge.setCurrent(badge.getTotal());
        userBadge.setUnlockedAt(LocalDateTime.now());
        
        userBadgeRepository.save(userBadge);
    }

    /**
     * Vérifie si un utilisateur possède déjà un badge
     */
    private boolean userHasBadge(User user, Badge badge) {
        Optional<UserBadge> userBadge = userBadgeRepository.findByUser_UserIdAndBadge_Id(user.getUserId(), badge.getId());
        return userBadge.isPresent() && userBadge.get().getUnlocked();
    }

    // Méthodes de comptage pour les différents critères
    
    private int countCompletedExercises(User user) {
        return (int) userExerciseRepository.countByUser_UserIdAndSuccess(user.getUserId(), true);
    }

    private int countSuccessfulSubmissions(User user) {
        return (int) submissionRepository.countByUser_UserIdAndIsCorrect(user.getUserId(), true);
    }

    private int countLanguageExercises(User user, String language) {
        // Cette méthode nécessiterait d'ajouter le language aux exercices
        // Pour l'instant, on retourne le nombre d'exercices complétés
        return countCompletedExercises(user);
    }

    private int countCompletedLessons(User user) {
        // Cette méthode nécessiterait un système de tracking des leçons
        // Pour l'instant, on utilise les exercices comme proxy
        return countCompletedExercises(user);
    }

    private int countPerfectScores(User user) {
        // Compte les soumissions avec score parfait
        return (int) submissionRepository.countByUser_UserIdAndIsCorrect(user.getUserId(), true);
    }

    /**
     * Évalue les badges après une action spécifique
     */
    public List<Badge> evaluateBadgesAfterAction(User user, String actionType) {
        // Cette méthode peut être appelée après différentes actions
        // pour déclencher une évaluation ciblée
        return evaluateAndAssignBadges(user);
    }
}