package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.repository.BadgeRepository;
import com.learncode.schoolDev.repository.ExerciseRepository;
import com.learncode.schoolDev.repository.LessonRepository;
import com.learncode.schoolDev.repository.ProgressRepository;
import com.learncode.schoolDev.repository.UserBadgeRepository;
import com.learncode.schoolDev.repository.UserExerciseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeEvaluationServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ProgressRepository progressRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private UserExerciseRepository userExerciseRepository;

    private BadgeEvaluationService badgeEvaluationService;

    private User testUser;
    private Badge testBadge;

    @BeforeEach
    void setUp() {
        badgeEvaluationService = new BadgeEvaluationService(
            badgeRepository,
            exerciseRepository,
            lessonRepository,
            progressRepository,
            userBadgeRepository,
            userExerciseRepository
        );

        // Données de test
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");

        testBadge = new Badge();
        testBadge.setId(1L);
        testBadge.setTitle("Test Badge");
        testBadge.setTotal(10);
        testBadge.setUnlockRequirement("exercises_completed:10");
    }

    @Test
    void testEvaluateAndAssignBadges_NoEligibleBadges() {
        // Arrange
        when(badgeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Badge> result = badgeEvaluationService.evaluateAndAssignBadges(testUser);

        // Assert
        assertTrue(result.isEmpty());
        verify(badgeRepository).findAll();
    }

    @Test
    void testEvaluateAndAssignBadges_UserAlreadyHasBadge() {
        // Arrange
        UserBadge existingUserBadge = new UserBadge();
        existingUserBadge.setUnlocked(true);
        
        when(badgeRepository.findAll()).thenReturn(Arrays.asList(testBadge));
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.of(existingUserBadge));

        // Act
        List<Badge> result = badgeEvaluationService.evaluateAndAssignBadges(testUser);

        // Assert
        assertTrue(result.isEmpty());
        // La méthode updateBadgeProgress sera appelée même si le badge est déjà possédé
        verify(userBadgeRepository).save(existingUserBadge);
    }

    @Test
    void testEvaluateAndAssignBadges_NewBadgeEarned() {
        // Arrange
        when(badgeRepository.findAll()).thenReturn(Arrays.asList(testBadge));
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.empty());
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(15L); // Plus que requis

        // Act
        List<Badge> result = badgeEvaluationService.evaluateAndAssignBadges(testUser);

        // Assert
        assertEquals(1, result.size());
        assertEquals(testBadge, result.get(0));
        verify(userBadgeRepository).save(any(UserBadge.class));
    }

    @Test
    void testEvaluateAndAssignBadges_ProgressUpdated() {
        // Arrange
        UserBadge existingUserBadge = new UserBadge();
        existingUserBadge.setUnlocked(false);
        existingUserBadge.setCurrent(5);
        
        when(badgeRepository.findAll()).thenReturn(Arrays.asList(testBadge));
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.of(existingUserBadge));
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(7L); // Pas encore assez

        // Act
        List<Badge> result = badgeEvaluationService.evaluateAndAssignBadges(testUser);

        // Assert
        assertTrue(result.isEmpty());
        verify(userBadgeRepository).save(existingUserBadge);
        assertEquals(7, existingUserBadge.getCurrent());
    }

    @Test
    void testEvaluateBadgeCondition_NullUnlockRequirement() {
        // Arrange
        Badge badgeWithoutRequirement = new Badge();
        badgeWithoutRequirement.setUnlockRequirement(null);

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, badgeWithoutRequirement);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEvaluateBadgeCondition_EmptyUnlockRequirement() {
        // Arrange
        Badge badgeWithEmptyRequirement = new Badge();
        badgeWithEmptyRequirement.setUnlockRequirement("   ");

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, badgeWithEmptyRequirement);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEvaluateBadgeCondition_ValidCondition_Met() {
        // Arrange
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(15L);

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, testBadge);

        // Assert
        assertTrue(result);
    }

    @Test
    void testEvaluateBadgeCondition_ValidCondition_NotMet() {
        // Arrange
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(5L);

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, testBadge);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEvaluateBadgeCondition_InvalidConditionFormat() {
        // Arrange
        Badge badgeWithInvalidRequirement = new Badge();
        badgeWithInvalidRequirement.setUnlockRequirement("invalid_format");

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, badgeWithInvalidRequirement);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEvaluateBadgeCondition_CoursesFinished() {
        // Arrange
        Badge coursesBadge = new Badge();
        coursesBadge.setUnlockRequirement("courses_finished:3");
        
        when(progressRepository.countByUser_UserIdAndPercentageCompletedGreaterThanEqual(1L, 100.0))
            .thenReturn(5L);

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, coursesBadge);

        // Assert
        assertTrue(result);
    }

    @Test
    void testEvaluateBadgeCondition_LanguageExercises() {
        // Arrange
        Badge languageBadge = new Badge();
        languageBadge.setUnlockRequirement("language_exercises:5:JAVA");
        
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(10L);

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, languageBadge);

        // Assert
        assertTrue(result);
    }

    @Test
    void testEvaluateBadgeCondition_LanguageExercises_NoLanguage() {
        // Arrange
        Badge languageBadge = new Badge();
        languageBadge.setUnlockRequirement("language_exercises:5");

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, languageBadge);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEvaluateBadgeCondition_LessonsCompleted() {
        // Arrange
        Badge lessonsBadge = new Badge();
        lessonsBadge.setUnlockRequirement("lessons_completed:3");
        
        when(userExerciseRepository.countFullyCompletedLessons(1L))
            .thenReturn(4L);

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, lessonsBadge);

        // Assert
        assertTrue(result);
    }

    @Test
    void testEvaluateBadgeCondition_UnknownConditionType() {
        // Arrange
        Badge unknownBadge = new Badge();
        unknownBadge.setUnlockRequirement("unknown_type:10");

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, unknownBadge);

        // Assert
        // Le parsing retourne false quand le type n'existe pas
        assertFalse(result);
    }

    @Test
    void testEvaluateBadgeCondition_InvalidNumberFormat() {
        // Arrange
        Badge invalidNumberBadge = new Badge();
        invalidNumberBadge.setUnlockRequirement("exercises_completed:not_a_number");

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, invalidNumberBadge);

        // Assert
        assertFalse(result);
    }

    @Test
    void testUpdateBadgeProgress_UserBadgeNotExists() {
        // Arrange
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.empty());

        // Act
        badgeEvaluationService.updateBadgeProgress(testUser, testBadge);

        // Assert
        verify(userBadgeRepository, never()).save(any(UserBadge.class));
    }

    @Test
    void testUpdateBadgeProgress_BadgeUnlocked() {
        // Arrange
        UserBadge userBadge = new UserBadge();
        userBadge.setUnlocked(false);
        userBadge.setCurrent(8);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.of(userBadge));
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(12L); // Suffisant pour débloquer

        // Act
        badgeEvaluationService.updateBadgeProgress(testUser, testBadge);

        // Assert
        assertTrue(userBadge.getUnlocked());
        assertNotNull(userBadge.getUnlockedAt());
        assertEquals(10, userBadge.getCurrent()); // Math.min(12, 10)
        verify(userBadgeRepository).save(userBadge);
    }

    @Test
    void testUpdateBadgeProgress_ProgressUpdatedOnly() {
        // Arrange
        UserBadge userBadge = new UserBadge();
        userBadge.setUnlocked(false);
        userBadge.setCurrent(5);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.of(userBadge));
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(7L); // Pas encore suffisant

        // Act
        badgeEvaluationService.updateBadgeProgress(testUser, testBadge);

        // Assert
        assertFalse(userBadge.getUnlocked());
        assertNull(userBadge.getUnlockedAt());
        assertEquals(7, userBadge.getCurrent());
        verify(userBadgeRepository).save(userBadge);
    }

    @Test
    void testEvaluateBadgesAfterAction() {
        // Arrange
        when(badgeRepository.findAll()).thenReturn(Arrays.asList(testBadge));
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.empty());
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(15L);

        // Act
        List<Badge> result = badgeEvaluationService.evaluateBadgesAfterAction(testUser, "test_action");

        // Assert
        assertEquals(1, result.size());
        assertEquals(testBadge, result.get(0));
    }

    @Test
    void testParseCondition_ValidSimpleFormat() {
        // Test via evaluateBadgeCondition pour tester parseCondition indirectement
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(15L);

        Badge badge = new Badge();
        badge.setUnlockRequirement("exercises_completed:10");
        
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, badge);
        
        assertTrue(result);
    }

    @Test
    void testParseCondition_ValidLanguageFormat() {
        // Test via evaluateBadgeCondition pour tester parseCondition indirectement
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(15L);

        Badge badge = new Badge();
        badge.setUnlockRequirement("language_exercises:5:PYTHON");
        
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, badge);
        
        assertTrue(result);
    }

    @Test
    void testCalculateCurrentProgress_AllConditionTypes() {
        // Arrange
        UserBadge userBadge = new UserBadge();
        userBadge.setUnlocked(false);

        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.of(userBadge));

        // Test EXERCISES_COMPLETED
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(7L);
        badgeEvaluationService.updateBadgeProgress(testUser, testBadge);
        assertEquals(7, userBadge.getCurrent());

        // Test COURSES_FINISHED
        Badge coursesBadge = new Badge();
        coursesBadge.setId(2L);
        coursesBadge.setUnlockRequirement("courses_finished:3");
        coursesBadge.setTotal(3);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 2L))
            .thenReturn(Optional.of(userBadge));
        when(progressRepository.countByUser_UserIdAndPercentageCompletedGreaterThanEqual(1L, 100.0))
            .thenReturn(2L);
            
        badgeEvaluationService.updateBadgeProgress(testUser, coursesBadge);
        assertEquals(2, userBadge.getCurrent());

        // Test LESSONS_COMPLETED
        Badge lessonsBadge = new Badge();
        lessonsBadge.setId(3L);
        lessonsBadge.setUnlockRequirement("lessons_completed:5");
        lessonsBadge.setTotal(5);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 3L))
            .thenReturn(Optional.of(userBadge));
        when(userExerciseRepository.countFullyCompletedLessons(1L))
            .thenReturn(4L);
            
        badgeEvaluationService.updateBadgeProgress(testUser, lessonsBadge);
        assertEquals(4, userBadge.getCurrent());
    }

    @Test
    void testCalculateCurrentProgress_NullUnlockRequirement() {
        // Arrange
        Badge badgeWithoutRequirement = new Badge();
        badgeWithoutRequirement.setId(2L);
        badgeWithoutRequirement.setUnlockRequirement(null);
        
        UserBadge userBadge = new UserBadge();
        userBadge.setUnlocked(false);
        userBadge.setCurrent(5);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 2L))
            .thenReturn(Optional.of(userBadge));

        // Act
        badgeEvaluationService.updateBadgeProgress(testUser, badgeWithoutRequirement);

        // Assert
        assertEquals(0, userBadge.getCurrent());
    }

    @Test
    void testCalculateCurrentProgress_InvalidCondition() {
        // Arrange
        Badge badgeWithInvalidRequirement = new Badge();
        badgeWithInvalidRequirement.setId(2L);
        badgeWithInvalidRequirement.setUnlockRequirement("invalid");
        
        UserBadge userBadge = new UserBadge();
        userBadge.setUnlocked(false);
        userBadge.setCurrent(5);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 2L))
            .thenReturn(Optional.of(userBadge));

        // Act
        badgeEvaluationService.updateBadgeProgress(testUser, badgeWithInvalidRequirement);

        // Assert
        assertEquals(0, userBadge.getCurrent());
    }

    @Test
    void testCalculateCurrentProgress_LanguageExercises_NoLanguage() {
        // Arrange
        Badge languageBadge = new Badge();
        languageBadge.setId(2L);
        languageBadge.setUnlockRequirement("language_exercises:5");
        languageBadge.setTotal(5);
        
        UserBadge userBadge = new UserBadge();
        userBadge.setUnlocked(false);
        userBadge.setCurrent(2);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 2L))
            .thenReturn(Optional.of(userBadge));

        // Act
        badgeEvaluationService.updateBadgeProgress(testUser, languageBadge);

        // Assert
        assertEquals(0, userBadge.getCurrent());
    }

    @Test
    void testUserHasBadge_UserHasUnlockedBadge() {
        // Arrange
        UserBadge unlockedUserBadge = new UserBadge();
        unlockedUserBadge.setUnlocked(true);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.of(unlockedUserBadge));
        when(badgeRepository.findAll()).thenReturn(Arrays.asList(testBadge));

        // Act
        List<Badge> result = badgeEvaluationService.evaluateAndAssignBadges(testUser);

        // Assert
        assertTrue(result.isEmpty()); // Badge déjà possédé
    }

    @Test
    void testUserHasBadge_UserHasLockedBadge() {
        // Arrange
        UserBadge lockedUserBadge = new UserBadge();
        lockedUserBadge.setUnlocked(false);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.of(lockedUserBadge));
        when(badgeRepository.findAll()).thenReturn(Arrays.asList(testBadge));
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(15L);

        // Act
        List<Badge> result = badgeEvaluationService.evaluateAndAssignBadges(testUser);

        // Assert
        assertEquals(1, result.size()); // Badge peut être débloqué
    }

    @Test
    void testAssignBadgeToUser() {
        // Arrange
        when(badgeRepository.findAll()).thenReturn(Arrays.asList(testBadge));
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 1L))
            .thenReturn(Optional.empty());
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(15L);

        // Act
        List<Badge> result = badgeEvaluationService.evaluateAndAssignBadges(testUser);

        // Assert
        verify(userBadgeRepository).save(argThat(userBadge -> 
            userBadge.getUser().equals(testUser) &&
            userBadge.getBadge().equals(testBadge) &&
            userBadge.getUnlocked() &&
            userBadge.getCurrent().equals(testBadge.getTotal()) &&
            userBadge.getUnlockedAt() != null
        ));
    }

    @Test
    void testEvaluateCondition_DefaultCase() {
        // Arrange - Badge with TOTAL_XP condition type
        Badge totalXpBadge = new Badge();
        totalXpBadge.setUnlockRequirement("total_xp:1000");

        // Act
        boolean result = badgeEvaluationService.evaluateBadgeCondition(testUser, totalXpBadge);

        // Assert
        // TOTAL_XP isn't implemented in evaluateCondition, so it should return false
        assertFalse(result);
    }

    @Test
    void testCalculateCurrentProgress_DefaultCase() {
        // Arrange
        Badge totalXpBadge = new Badge();
        totalXpBadge.setId(4L);
        totalXpBadge.setUnlockRequirement("total_xp:1000");
        totalXpBadge.setTotal(1000);
        
        UserBadge userBadge = new UserBadge();
        userBadge.setUnlocked(false);
        userBadge.setCurrent(100);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 4L))
            .thenReturn(Optional.of(userBadge));

        // Act
        badgeEvaluationService.updateBadgeProgress(testUser, totalXpBadge);

        // Assert
        assertEquals(0, userBadge.getCurrent()); // Default case returns 0
    }

    @Test
    void testCalculateCurrentProgress_LanguageExercises_WithLanguage() {
        // Arrange
        Badge languageBadge = new Badge();
        languageBadge.setId(5L);
        languageBadge.setUnlockRequirement("language_exercises:8:JAVA");
        languageBadge.setTotal(8);
        
        UserBadge userBadge = new UserBadge();
        userBadge.setUnlocked(false);
        userBadge.setCurrent(3);
        
        when(userBadgeRepository.findByUser_UserIdAndBadge_Id(1L, 5L))
            .thenReturn(Optional.of(userBadge));
        when(userExerciseRepository.countByUser_UserIdAndSuccess(1L, true))
            .thenReturn(6L);

        // Act
        badgeEvaluationService.updateBadgeProgress(testUser, languageBadge);

        // Assert
        assertEquals(6, userBadge.getCurrent());
    }
}