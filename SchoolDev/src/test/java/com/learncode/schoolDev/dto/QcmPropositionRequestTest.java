package com.learncode.schoolDev.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

@DisplayName("Tests pour QcmPropositionRequest")
class QcmPropositionRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Constructeur par défaut")
    void testDefaultConstructor() {
        // Given & When
        QcmPropositionRequest request = new QcmPropositionRequest();

        // Then
        assertNull(request.getText());
        assertNull(request.getIsCorrect());
    }

    @Test
    @DisplayName("Setter et getter pour text")
    void testTextSetterGetter() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        String expectedText = "Cette proposition est correcte";

        // When
        request.setText(expectedText);

        // Then
        assertEquals(expectedText, request.getText());
    }

    @Test
    @DisplayName("Setter et getter pour isCorrect")
    void testIsCorrectSetterGetter() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();

        // When & Then - Test avec true
        request.setIsCorrect(true);
        assertTrue(request.getIsCorrect());

        // When & Then - Test avec false
        request.setIsCorrect(false);
        assertFalse(request.getIsCorrect());

        // When & Then - Test avec null
        request.setIsCorrect(null);
        assertNull(request.getIsCorrect());
    }

    @Test
    @DisplayName("Validation réussie avec données valides")
    void testValidationSuccess() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        request.setText("Proposition valide");
        request.setIsCorrect(true);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Validation échoue avec text null")
    void testValidationFailsWithNullText() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        request.setText(null);
        request.setIsCorrect(true);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<QcmPropositionRequest> violation = violations.iterator().next();
        assertEquals("Le texte de la proposition est obligatoire", violation.getMessage());
        assertEquals("text", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Validation échoue avec text vide")
    void testValidationFailsWithEmptyText() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        request.setText("");
        request.setIsCorrect(true);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<QcmPropositionRequest> violation = violations.iterator().next();
        assertEquals("Le texte de la proposition est obligatoire", violation.getMessage());
    }

    @Test
    @DisplayName("Validation échoue avec text contenant seulement des espaces")
    void testValidationFailsWithBlankText() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        request.setText("   ");
        request.setIsCorrect(true);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<QcmPropositionRequest> violation = violations.iterator().next();
        assertEquals("Le texte de la proposition est obligatoire", violation.getMessage());
    }

    @Test
    @DisplayName("Validation échoue avec isCorrect null")
    void testValidationFailsWithNullIsCorrect() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        request.setText("Proposition valide");
        request.setIsCorrect(null);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<QcmPropositionRequest> violation = violations.iterator().next();
        assertEquals("Il faut indiquer si la proposition est correcte", violation.getMessage());
        assertEquals("isCorrect", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Validation échoue avec text null ET isCorrect null")
    void testValidationFailsWithBothNull() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        request.setText(null);
        request.setIsCorrect(null);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertEquals(2, violations.size());
        
        // Vérifier que les deux violations sont présentes
        boolean hasTextViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Le texte de la proposition est obligatoire"));
        boolean hasIsCorrectViolation = violations.stream()
            .anyMatch(v -> v.getMessage().equals("Il faut indiquer si la proposition est correcte"));
        
        assertTrue(hasTextViolation);
        assertTrue(hasIsCorrectViolation);
    }

    @Test
    @DisplayName("Validation réussie avec proposition correcte")
    void testValidationSuccessWithCorrectProposition() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        request.setText("La réponse correcte");
        request.setIsCorrect(true);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
        assertTrue(request.getIsCorrect());
    }

    @Test
    @DisplayName("Validation réussie avec proposition incorrecte")
    void testValidationSuccessWithIncorrectProposition() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        request.setText("La réponse incorrecte");
        request.setIsCorrect(false);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
        assertFalse(request.getIsCorrect());
    }

    @Test
    @DisplayName("Test avec des caractères spéciaux dans le texte")
    void testWithSpecialCharacters() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        String textWithSpecialChars = "Réponse avec accents éàü et symboles @#$%^&*()";
        request.setText(textWithSpecialChars);
        request.setIsCorrect(true);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
        assertEquals(textWithSpecialChars, request.getText());
    }

    @Test
    @DisplayName("Test avec un texte très long")
    void testWithLongText() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        String longText = "A".repeat(1000); // Texte de 1000 caractères
        request.setText(longText);
        request.setIsCorrect(false);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longText, request.getText());
        assertEquals(1000, request.getText().length());
    }

    @Test
    @DisplayName("Test avec un texte contenant des sauts de ligne")
    void testWithMultilineText() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        String multilineText = "Première ligne\nDeuxième ligne\nTroisième ligne";
        request.setText(multilineText);
        request.setIsCorrect(true);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
        assertEquals(multilineText, request.getText());
    }

    @Test
    @DisplayName("Test modification des propriétés")
    void testPropertyModification() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        
        // When & Then - Modification du texte
        request.setText("Premier texte");
        assertEquals("Premier texte", request.getText());
        
        request.setText("Deuxième texte");
        assertEquals("Deuxième texte", request.getText());
        
        // When & Then - Modification de isCorrect
        request.setIsCorrect(true);
        assertTrue(request.getIsCorrect());
        
        request.setIsCorrect(false);
        assertFalse(request.getIsCorrect());
    }

    @Test
    @DisplayName("Test avec un texte d'un seul caractère")
    void testWithSingleCharacterText() {
        // Given
        QcmPropositionRequest request = new QcmPropositionRequest();
        request.setText("A");
        request.setIsCorrect(true);

        // When
        Set<ConstraintViolation<QcmPropositionRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
        assertEquals("A", request.getText());
    }
}