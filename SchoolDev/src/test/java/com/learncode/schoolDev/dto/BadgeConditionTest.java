package com.learncode.schoolDev.dto;

import com.learncode.schoolDev.enums.BadgeConditionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadgeConditionTest {

    @Test
    void testDefaultConstructor() {
        BadgeCondition condition = new BadgeCondition();
        assertNull(condition.getType());
        assertEquals(0, condition.getTargetValue());
        assertNull(condition.getLanguage());
        assertNull(condition.getAdditionalData());
    }

    @Test
    void testConstructorWithTypeAndTargetValue() {
        BadgeConditionType type = BadgeConditionType.EXERCISES_COMPLETED;
        int targetValue = 10;
        
        BadgeCondition condition = new BadgeCondition(type, targetValue);
        
        assertEquals(type, condition.getType());
        assertEquals(targetValue, condition.getTargetValue());
        assertNull(condition.getLanguage());
        assertNull(condition.getAdditionalData());
    }

    @Test
    void testConstructorWithTypeTargetValueAndLanguage() {
        BadgeConditionType type = BadgeConditionType.LANGUAGE_EXERCISES;
        int targetValue = 5;
        String language = "JAVA";
        
        BadgeCondition condition = new BadgeCondition(type, targetValue, language);
        
        assertEquals(type, condition.getType());
        assertEquals(targetValue, condition.getTargetValue());
        assertEquals(language, condition.getLanguage());
        assertNull(condition.getAdditionalData());
    }

    @Test
    void testSetType() {
        BadgeCondition condition = new BadgeCondition();
        BadgeConditionType type = BadgeConditionType.COURSES_FINISHED;
        
        condition.setType(type);
        
        assertEquals(type, condition.getType());
    }

    @Test
    void testSetTargetValue() {
        BadgeCondition condition = new BadgeCondition();
        int targetValue = 25;
        
        condition.setTargetValue(targetValue);
        
        assertEquals(targetValue, condition.getTargetValue());
    }

    @Test
    void testSetLanguage() {
        BadgeCondition condition = new BadgeCondition();
        String language = "PYTHON";
        
        condition.setLanguage(language);
        
        assertEquals(language, condition.getLanguage());
    }

    @Test
    void testSetAdditionalData() {
        BadgeCondition condition = new BadgeCondition();
        String additionalData = "extra_info";
        
        condition.setAdditionalData(additionalData);
        
        assertEquals(additionalData, condition.getAdditionalData());
    }

    @Test
    void testToString() {
        BadgeCondition condition = new BadgeCondition(
            BadgeConditionType.LANGUAGE_EXERCISES, 
            15, 
            "JAVASCRIPT"
        );
        condition.setAdditionalData("bonus_points");
        
        String result = condition.toString();
        
        assertTrue(result.contains("BadgeCondition{"));
        assertTrue(result.contains("type=LANGUAGE_EXERCISES"));
        assertTrue(result.contains("targetValue=15"));
        assertTrue(result.contains("language='JAVASCRIPT'"));
        assertTrue(result.contains("additionalData='bonus_points'"));
    }

    @Test
    void testToStringWithNullValues() {
        BadgeCondition condition = new BadgeCondition();
        
        String result = condition.toString();
        
        assertTrue(result.contains("BadgeCondition{"));
        assertTrue(result.contains("type=null"));
        assertTrue(result.contains("targetValue=0"));
        assertTrue(result.contains("language='null'"));
        assertTrue(result.contains("additionalData='null'"));
    }

    @Test
    void testAllBadgeConditionTypes() {
        // Test avec différents types pour couvrir tous les cas
        BadgeCondition condition1 = new BadgeCondition(BadgeConditionType.EXERCISES_COMPLETED, 10);
        assertEquals(BadgeConditionType.EXERCISES_COMPLETED, condition1.getType());
        
        BadgeCondition condition2 = new BadgeCondition(BadgeConditionType.COURSES_FINISHED, 3);
        assertEquals(BadgeConditionType.COURSES_FINISHED, condition2.getType());
        
        BadgeCondition condition3 = new BadgeCondition(BadgeConditionType.LESSONS_COMPLETED, 5);
        assertEquals(BadgeConditionType.LESSONS_COMPLETED, condition3.getType());
        
        BadgeCondition condition4 = new BadgeCondition(BadgeConditionType.TOTAL_XP, 1000);
        assertEquals(BadgeConditionType.TOTAL_XP, condition4.getType());
    }

    @Test
    void testCompleteWorkflow() {
        // Test d'un workflow complet avec modification des valeurs
        BadgeCondition condition = new BadgeCondition();
        
        // Modification progressive de tous les champs
        condition.setType(BadgeConditionType.LANGUAGE_EXERCISES);
        condition.setTargetValue(20);
        condition.setLanguage("JAVA");
        condition.setAdditionalData("advanced_level");
        
        // Vérifications
        assertEquals(BadgeConditionType.LANGUAGE_EXERCISES, condition.getType());
        assertEquals(20, condition.getTargetValue());
        assertEquals("JAVA", condition.getLanguage());
        assertEquals("advanced_level", condition.getAdditionalData());
        
        // Test toString avec toutes les valeurs renseignées
        String stringResult = condition.toString();
        assertTrue(stringResult.contains("LANGUAGE_EXERCISES"));
        assertTrue(stringResult.contains("20"));
        assertTrue(stringResult.contains("JAVA"));
        assertTrue(stringResult.contains("advanced_level"));
    }

    @Test
    void testEdgeCases() {
        BadgeCondition condition = new BadgeCondition();
        
        // Test avec valeur négative
        condition.setTargetValue(-5);
        assertEquals(-5, condition.getTargetValue());
        
        // Test avec chaîne vide
        condition.setLanguage("");
        assertEquals("", condition.getLanguage());
        
        condition.setAdditionalData("");
        assertEquals("", condition.getAdditionalData());
        
        // Test avec valeur maximale
        condition.setTargetValue(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, condition.getTargetValue());
    }
}