package com.learncode.schoolDev.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests pour QcmPropositionResponse")
class QcmPropositionResponseTest {

    @Test
    @DisplayName("Constructeur par défaut")
    void testDefaultConstructor() {
        // Given & When
        QcmPropositionResponse response = new QcmPropositionResponse();

        // Then
        assertNull(response.getPropositionId());
        assertNull(response.getText());
        assertFalse(response.isCorrect()); // boolean par défaut = false
    }

    @Test
    @DisplayName("Constructeur avec paramètres")
    void testConstructorWithParameters() {
        // Given
        Long propositionId = 1L;
        String text = "Réponse A";
        boolean isCorrect = true;

        // When
        QcmPropositionResponse response = new QcmPropositionResponse(propositionId, text, isCorrect);

        // Then
        assertEquals(propositionId, response.getPropositionId());
        assertEquals(text, response.getText());
        assertTrue(response.isCorrect());
    }

    @Test
    @DisplayName("Constructeur avec paramètres - réponse incorrecte")
    void testConstructorWithIncorrectAnswer() {
        // Given
        Long propositionId = 2L;
        String text = "Réponse B";
        boolean isCorrect = false;

        // When
        QcmPropositionResponse response = new QcmPropositionResponse(propositionId, text, isCorrect);

        // Then
        assertEquals(propositionId, response.getPropositionId());
        assertEquals(text, response.getText());
        assertFalse(response.isCorrect());
    }

    @Test
    @DisplayName("Setter et getter pour propositionId")
    void testPropositionIdSetterGetter() {
        // Given
        QcmPropositionResponse response = new QcmPropositionResponse();
        Long expectedId = 42L;

        // When
        response.setPropositionId(expectedId);

        // Then
        assertEquals(expectedId, response.getPropositionId());
    }

    @Test
    @DisplayName("Setter et getter pour text")
    void testTextSetterGetter() {
        // Given
        QcmPropositionResponse response = new QcmPropositionResponse();
        String expectedText = "Cette réponse est correcte";

        // When
        response.setText(expectedText);

        // Then
        assertEquals(expectedText, response.getText());
    }

    @Test
    @DisplayName("Setter et getter pour isCorrect")
    void testIsCorrectSetterGetter() {
        // Given
        QcmPropositionResponse response = new QcmPropositionResponse();

        // When & Then - Test avec true
        response.setCorrect(true);
        assertTrue(response.isCorrect());

        // When & Then - Test avec false
        response.setCorrect(false);
        assertFalse(response.isCorrect());
    }

    @Test
    @DisplayName("Test avec des valeurs nulles")
    void testWithNullValues() {
        // Given & When
        QcmPropositionResponse response = new QcmPropositionResponse(null, null, false);

        // Then
        assertNull(response.getPropositionId());
        assertNull(response.getText());
        assertFalse(response.isCorrect());
    }

    @Test
    @DisplayName("Test modification après construction")
    void testModificationAfterConstruction() {
        // Given
        QcmPropositionResponse response = new QcmPropositionResponse(1L, "Initial", true);

        // When
        response.setPropositionId(2L);
        response.setText("Modified");
        response.setCorrect(false);

        // Then
        assertEquals(2L, response.getPropositionId());
        assertEquals("Modified", response.getText());
        assertFalse(response.isCorrect());
    }

    @Test
    @DisplayName("Test avec des caractères spéciaux dans le texte")
    void testWithSpecialCharacters() {
        // Given
        String textWithSpecialChars = "Réponse avec accents éàü et symboles @#$%^&*()";
        
        // When
        QcmPropositionResponse response = new QcmPropositionResponse(1L, textWithSpecialChars, true);

        // Then
        assertEquals(textWithSpecialChars, response.getText());
    }

    @Test
    @DisplayName("Test avec un texte vide")
    void testWithEmptyText() {
        // Given
        String emptyText = "";
        
        // When
        QcmPropositionResponse response = new QcmPropositionResponse(1L, emptyText, false);

        // Then
        assertEquals(emptyText, response.getText());
        assertEquals("", response.getText());
    }

    @Test
    @DisplayName("Test avec un ID négatif")
    void testWithNegativeId() {
        // Given
        Long negativeId = -1L;
        
        // When
        QcmPropositionResponse response = new QcmPropositionResponse(negativeId, "Test", true);

        // Then
        assertEquals(negativeId, response.getPropositionId());
    }

    @Test
    @DisplayName("Test avec un très grand ID")
    void testWithLargeId() {
        // Given
        Long largeId = Long.MAX_VALUE;
        
        // When
        QcmPropositionResponse response = new QcmPropositionResponse(largeId, "Test", false);

        // Then
        assertEquals(largeId, response.getPropositionId());
    }

    @Test
    @DisplayName("Test d'égalité des objets (vérification des propriétés)")
    void testObjectProperties() {
        // Given
        Long id = 1L;
        String text = "Test égalité";
        boolean isCorrect = true;

        QcmPropositionResponse response1 = new QcmPropositionResponse(id, text, isCorrect);
        QcmPropositionResponse response2 = new QcmPropositionResponse();
        response2.setPropositionId(id);
        response2.setText(text);
        response2.setCorrect(isCorrect);

        // Then
        assertEquals(response1.getPropositionId(), response2.getPropositionId());
        assertEquals(response1.getText(), response2.getText());
        assertEquals(response1.isCorrect(), response2.isCorrect());
    }
}