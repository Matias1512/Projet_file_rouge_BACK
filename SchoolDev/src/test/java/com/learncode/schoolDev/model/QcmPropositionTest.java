package com.learncode.schoolDev.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

@DisplayName("Tests pour QcmProposition")
class QcmPropositionTest {

    private Validator validator;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // Créer un exercice de test
        exercise = new Exercise();
        exercise.setExerciseId(1L);
        exercise.setTitle("Test Exercise");
        exercise.setDescription("Description test");
        exercise.setType(ExerciseType.QCM);
    }

    @Test
    @DisplayName("Constructeur par défaut")
    void testDefaultConstructor() {
        // Given & When
        QcmProposition proposition = new QcmProposition();

        // Then
        assertNull(proposition.getPropositionId());
        assertNull(proposition.getText());
        assertFalse(proposition.isCorrect()); // boolean par défaut = false
        assertNull(proposition.getExercise());
    }

    @Test
    @DisplayName("Setter et getter pour propositionId")
    void testPropositionIdSetterGetter() {
        // Given
        QcmProposition proposition = new QcmProposition();
        Long expectedId = 42L;

        // When
        proposition.setPropositionId(expectedId);

        // Then
        assertEquals(expectedId, proposition.getPropositionId());
    }

    @Test
    @DisplayName("Setter et getter pour text")
    void testTextSetterGetter() {
        // Given
        QcmProposition proposition = new QcmProposition();
        String expectedText = "Cette proposition est correcte";

        // When
        proposition.setText(expectedText);

        // Then
        assertEquals(expectedText, proposition.getText());
    }

    @Test
    @DisplayName("Setter et getter pour isCorrect")
    void testIsCorrectSetterGetter() {
        // Given
        QcmProposition proposition = new QcmProposition();

        // When & Then - Test avec true
        proposition.setCorrect(true);
        assertTrue(proposition.isCorrect());

        // When & Then - Test avec false
        proposition.setCorrect(false);
        assertFalse(proposition.isCorrect());
    }

    @Test
    @DisplayName("Setter et getter pour exercise")
    void testExerciseSetterGetter() {
        // Given
        QcmProposition proposition = new QcmProposition();

        // When
        proposition.setExercise(exercise);

        // Then
        assertEquals(exercise, proposition.getExercise());
        assertEquals(1L, proposition.getExercise().getExerciseId());
    }

    @Test
    @DisplayName("Validation réussie avec données valides")
    void testValidationSuccess() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setText("Proposition valide");
        proposition.setCorrect(true);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Validation échoue avec text null")
    void testValidationFailsWithNullText() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setText(null);
        proposition.setCorrect(true);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<QcmProposition> violation = violations.iterator().next();
        assertEquals("Le texte de la proposition est obligatoire", violation.getMessage());
        assertEquals("text", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Validation échoue avec text vide")
    void testValidationFailsWithEmptyText() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setText("");
        proposition.setCorrect(true);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<QcmProposition> violation = violations.iterator().next();
        assertEquals("Le texte de la proposition est obligatoire", violation.getMessage());
    }

    @Test
    @DisplayName("Validation échoue avec text contenant seulement des espaces")
    void testValidationFailsWithBlankText() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setText("   ");
        proposition.setCorrect(true);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<QcmProposition> violation = violations.iterator().next();
        assertEquals("Le texte de la proposition est obligatoire", violation.getMessage());
    }

    @Test
    @DisplayName("Test avec proposition correcte")
    void testCorrectProposition() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setText("La bonne réponse");
        proposition.setCorrect(true);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertTrue(violations.isEmpty());
        assertTrue(proposition.isCorrect());
        assertEquals("La bonne réponse", proposition.getText());
    }

    @Test
    @DisplayName("Test avec proposition incorrecte")
    void testIncorrectProposition() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setText("La mauvaise réponse");
        proposition.setCorrect(false);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertTrue(violations.isEmpty());
        assertFalse(proposition.isCorrect());
        assertEquals("La mauvaise réponse", proposition.getText());
    }

    @Test
    @DisplayName("Test relation avec Exercise - association")
    void testExerciseRelationAssociation() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setText("Test relation");
        proposition.setCorrect(true);

        // When
        proposition.setExercise(exercise);

        // Then
        assertNotNull(proposition.getExercise());
        assertEquals(exercise, proposition.getExercise());
        assertEquals("Test Exercise", proposition.getExercise().getTitle());
        assertEquals(ExerciseType.QCM, proposition.getExercise().getType());
    }

    @Test
    @DisplayName("Test relation avec Exercise - dissociation")
    void testExerciseRelationDissociation() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setExercise(exercise);

        // When
        proposition.setExercise(null);

        // Then
        assertNull(proposition.getExercise());
    }

    @Test
    @DisplayName("Test avec des caractères spéciaux dans le texte")
    void testWithSpecialCharacters() {
        // Given
        QcmProposition proposition = new QcmProposition();
        String textWithSpecialChars = "Réponse avec accents éàü et symboles @#$%^&*()";
        proposition.setText(textWithSpecialChars);
        proposition.setCorrect(true);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertTrue(violations.isEmpty());
        assertEquals(textWithSpecialChars, proposition.getText());
    }

    @Test
    @DisplayName("Test avec un texte très long")
    void testWithLongText() {
        // Given
        QcmProposition proposition = new QcmProposition();
        String longText = "A".repeat(1000); // Texte de 1000 caractères
        proposition.setText(longText);
        proposition.setCorrect(false);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longText, proposition.getText());
        assertEquals(1000, proposition.getText().length());
    }

    @Test
    @DisplayName("Test avec un texte d'un seul caractère")
    void testWithSingleCharacterText() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setText("A");
        proposition.setCorrect(true);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertTrue(violations.isEmpty());
        assertEquals("A", proposition.getText());
    }

    @Test
    @DisplayName("Test avec un texte contenant des sauts de ligne")
    void testWithMultilineText() {
        // Given
        QcmProposition proposition = new QcmProposition();
        String multilineText = "Première ligne\nDeuxième ligne\nTroisième ligne";
        proposition.setText(multilineText);
        proposition.setCorrect(true);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertTrue(violations.isEmpty());
        assertEquals(multilineText, proposition.getText());
    }

    @Test
    @DisplayName("Test modification des propriétés")
    void testPropertyModification() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setText("Texte initial");
        proposition.setCorrect(false);
        proposition.setExercise(exercise);

        // When & Then - Modification du texte
        proposition.setText("Nouveau texte");
        assertEquals("Nouveau texte", proposition.getText());

        // When & Then - Modification de isCorrect
        proposition.setCorrect(true);
        assertTrue(proposition.isCorrect());

        // When & Then - Modification de l'exercice
        Exercise newExercise = new Exercise();
        newExercise.setExerciseId(2L);
        newExercise.setTitle("Nouveau Exercise");
        
        proposition.setExercise(newExercise);
        assertEquals(newExercise, proposition.getExercise());
        assertEquals(2L, proposition.getExercise().getExerciseId());
    }

    @Test
    @DisplayName("Test avec propositionId null (nouveau objet)")
    void testWithNullId() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setText("Test sans ID");
        proposition.setCorrect(true);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertTrue(violations.isEmpty());
        assertNull(proposition.getPropositionId()); // ID null avant persistance
    }

    @Test
    @DisplayName("Test avec des IDs négatifs")
    void testWithNegativeIds() {
        // Given
        QcmProposition proposition = new QcmProposition();
        proposition.setPropositionId(-1L);
        proposition.setText("Test ID négatif");
        proposition.setCorrect(true);
        proposition.setExercise(exercise);

        // When
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);

        // Then
        assertTrue(violations.isEmpty()); // Pas de validation sur l'ID
        assertEquals(-1L, proposition.getPropositionId());
    }

    @Test
    @DisplayName("Test état initial de l'objet")
    void testInitialState() {
        // Given & When
        QcmProposition proposition = new QcmProposition();

        // Then
        assertNull(proposition.getPropositionId());
        assertNull(proposition.getText());
        assertFalse(proposition.isCorrect()); // boolean par défaut
        assertNull(proposition.getExercise());
    }

    @Test
    @DisplayName("Test configuration complète de l'objet")
    void testCompleteObjectConfiguration() {
        // Given
        QcmProposition proposition = new QcmProposition();
        Long propositionId = 123L;
        String text = "Configuration complète";
        boolean isCorrect = true;

        // When
        proposition.setPropositionId(propositionId);
        proposition.setText(text);
        proposition.setCorrect(isCorrect);
        proposition.setExercise(exercise);

        // Then
        assertEquals(propositionId, proposition.getPropositionId());
        assertEquals(text, proposition.getText());
        assertEquals(isCorrect, proposition.isCorrect());
        assertEquals(exercise, proposition.getExercise());
        
        // Validation
        Set<ConstraintViolation<QcmProposition>> violations = validator.validate(proposition);
        assertTrue(violations.isEmpty());
    }
}