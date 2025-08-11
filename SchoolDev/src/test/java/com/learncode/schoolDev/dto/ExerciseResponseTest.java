package com.learncode.schoolDev.dto;

import com.learncode.schoolDev.model.ExerciseType;
import com.learncode.schoolDev.model.Lesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseResponseTest {

    private ExerciseResponse exerciseResponse;
    private LocalDateTime testDateTime;
    private Lesson testLesson;
    private List<QcmPropositionResponse> testPropositions;

    @BeforeEach
    void setUp() {
        exerciseResponse = new ExerciseResponse();
        testDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        
        testLesson = new Lesson();
        testLesson.setLessonId(1L);
        testLesson.setTitle("Test Lesson");
        
        testPropositions = Arrays.asList(
            new QcmPropositionResponse(1L, "Option A", true),
            new QcmPropositionResponse(2L, "Option B", false)
        );
    }

    @Test
    void testDefaultConstructor() {
        // Given & When
        ExerciseResponse response = new ExerciseResponse();

        // Then
        assertNotNull(response);
        assertNull(response.getExerciseId());
        assertNull(response.getTitle());
        assertNull(response.getDescription());
        assertNull(response.getType());
        assertNull(response.getStarterCode());
        assertNull(response.getTestCases());
        assertNull(response.getCreatedAt());
        assertNull(response.getPropositions());
        assertNull(response.getLessonId());
        assertNull(response.getLesson());
    }

    @Test
    void testParameterizedConstructor() {
        // Given & When
        ExerciseResponse response = new ExerciseResponse(
            1L,
            "Test Exercise",
            "Test Description",
            ExerciseType.CODE,
            "public class Test {}",
            "test cases content",
            testDateTime
        );

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getExerciseId());
        assertEquals("Test Exercise", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals(ExerciseType.CODE, response.getType());
        assertEquals("public class Test {}", response.getStarterCode());
        assertEquals("test cases content", response.getTestCases());
        assertEquals(testDateTime, response.getCreatedAt());
        
        // Fields not set by constructor should be null
        assertNull(response.getPropositions());
        assertNull(response.getLessonId());
        assertNull(response.getLesson());
    }

    @Test
    void testParameterizedConstructorWithNullValues() {
        // Given & When
        ExerciseResponse response = new ExerciseResponse(
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        // Then
        assertNotNull(response);
        assertNull(response.getExerciseId());
        assertNull(response.getTitle());
        assertNull(response.getDescription());
        assertNull(response.getType());
        assertNull(response.getStarterCode());
        assertNull(response.getTestCases());
        assertNull(response.getCreatedAt());
    }

    @Test
    void testExerciseIdGetterAndSetter() {
        // Given
        Long expectedId = 42L;

        // When
        exerciseResponse.setExerciseId(expectedId);

        // Then
        assertEquals(expectedId, exerciseResponse.getExerciseId());
    }

    @Test
    void testExerciseIdSetterWithNull() {
        // Given & When
        exerciseResponse.setExerciseId(null);

        // Then
        assertNull(exerciseResponse.getExerciseId());
    }

    @Test
    void testTitleGetterAndSetter() {
        // Given
        String expectedTitle = "Advanced Java Exercise";

        // When
        exerciseResponse.setTitle(expectedTitle);

        // Then
        assertEquals(expectedTitle, exerciseResponse.getTitle());
    }

    @Test
    void testTitleSetterWithNull() {
        // Given & When
        exerciseResponse.setTitle(null);

        // Then
        assertNull(exerciseResponse.getTitle());
    }

    @Test
    void testDescriptionGetterAndSetter() {
        // Given
        String expectedDescription = "This is a comprehensive exercise description";

        // When
        exerciseResponse.setDescription(expectedDescription);

        // Then
        assertEquals(expectedDescription, exerciseResponse.getDescription());
    }

    @Test
    void testDescriptionSetterWithNull() {
        // Given & When
        exerciseResponse.setDescription(null);

        // Then
        assertNull(exerciseResponse.getDescription());
    }

    @Test
    void testTypeGetterAndSetterWithCODE() {
        // Given
        ExerciseType expectedType = ExerciseType.CODE;

        // When
        exerciseResponse.setType(expectedType);

        // Then
        assertEquals(expectedType, exerciseResponse.getType());
    }

    @Test
    void testTypeGetterAndSetterWithQCM() {
        // Given
        ExerciseType expectedType = ExerciseType.QCM;

        // When
        exerciseResponse.setType(expectedType);

        // Then
        assertEquals(expectedType, exerciseResponse.getType());
    }

    @Test
    void testTypeSetterWithNull() {
        // Given & When
        exerciseResponse.setType(null);

        // Then
        assertNull(exerciseResponse.getType());
    }

    @Test
    void testStarterCodeGetterAndSetter() {
        // Given
        String expectedStarterCode = "public class Solution {\n    public static void main(String[] args) {\n        // Your code here\n    }\n}";

        // When
        exerciseResponse.setStarterCode(expectedStarterCode);

        // Then
        assertEquals(expectedStarterCode, exerciseResponse.getStarterCode());
    }

    @Test
    void testStarterCodeSetterWithNull() {
        // Given & When
        exerciseResponse.setStarterCode(null);

        // Then
        assertNull(exerciseResponse.getStarterCode());
    }

    @Test
    void testTestCasesGetterAndSetter() {
        // Given
        String expectedTestCases = "assertEquals(42, solution.calculate(21, 21));";

        // When
        exerciseResponse.setTestCases(expectedTestCases);

        // Then
        assertEquals(expectedTestCases, exerciseResponse.getTestCases());
    }

    @Test
    void testTestCasesSetterWithNull() {
        // Given & When
        exerciseResponse.setTestCases(null);

        // Then
        assertNull(exerciseResponse.getTestCases());
    }

    @Test
    void testCreatedAtGetterAndSetter() {
        // Given & When
        exerciseResponse.setCreatedAt(testDateTime);

        // Then
        assertEquals(testDateTime, exerciseResponse.getCreatedAt());
    }

    @Test
    void testCreatedAtSetterWithNull() {
        // Given & When
        exerciseResponse.setCreatedAt(null);

        // Then
        assertNull(exerciseResponse.getCreatedAt());
    }

    @Test
    void testPropositionsGetterAndSetter() {
        // Given & When
        exerciseResponse.setPropositions(testPropositions);

        // Then
        assertEquals(testPropositions, exerciseResponse.getPropositions());
        assertEquals(2, exerciseResponse.getPropositions().size());
        assertEquals(1L, exerciseResponse.getPropositions().get(0).getPropositionId());
        assertEquals("Option A", exerciseResponse.getPropositions().get(0).getText());
        assertTrue(exerciseResponse.getPropositions().get(0).isCorrect());
    }

    @Test
    void testPropositionsSetterWithEmptyList() {
        // Given
        List<QcmPropositionResponse> emptyPropositions = Arrays.asList();

        // When
        exerciseResponse.setPropositions(emptyPropositions);

        // Then
        assertEquals(emptyPropositions, exerciseResponse.getPropositions());
        assertTrue(exerciseResponse.getPropositions().isEmpty());
    }

    @Test
    void testPropositionsSetterWithNull() {
        // Given & When
        exerciseResponse.setPropositions(null);

        // Then
        assertNull(exerciseResponse.getPropositions());
    }

    @Test
    void testLessonIdGetterAndSetter() {
        // Given
        Long expectedLessonId = 123L;

        // When
        exerciseResponse.setLessonId(expectedLessonId);

        // Then
        assertEquals(expectedLessonId, exerciseResponse.getLessonId());
    }

    @Test
    void testLessonIdSetterWithNull() {
        // Given & When
        exerciseResponse.setLessonId(null);

        // Then
        assertNull(exerciseResponse.getLessonId());
    }

    @Test
    void testLessonGetterAndSetter() {
        // Given & When
        exerciseResponse.setLesson(testLesson);

        // Then
        assertEquals(testLesson, exerciseResponse.getLesson());
        assertEquals(1L, exerciseResponse.getLesson().getLessonId());
        assertEquals("Test Lesson", exerciseResponse.getLesson().getTitle());
    }

    @Test
    void testLessonSetterWithNull() {
        // Given & When
        exerciseResponse.setLesson(null);

        // Then
        assertNull(exerciseResponse.getLesson());
    }

    @Test
    void testCompleteExerciseResponseBuild() {
        // Given
        Long exerciseId = 100L;
        String title = "Complete Exercise";
        String description = "Complete Description";
        ExerciseType type = ExerciseType.QCM;
        String starterCode = "// Complete starter code";
        String testCases = "// Complete test cases";
        Long lessonId = 200L;

        // When
        exerciseResponse.setExerciseId(exerciseId);
        exerciseResponse.setTitle(title);
        exerciseResponse.setDescription(description);
        exerciseResponse.setType(type);
        exerciseResponse.setStarterCode(starterCode);
        exerciseResponse.setTestCases(testCases);
        exerciseResponse.setCreatedAt(testDateTime);
        exerciseResponse.setPropositions(testPropositions);
        exerciseResponse.setLessonId(lessonId);
        exerciseResponse.setLesson(testLesson);

        // Then
        assertEquals(exerciseId, exerciseResponse.getExerciseId());
        assertEquals(title, exerciseResponse.getTitle());
        assertEquals(description, exerciseResponse.getDescription());
        assertEquals(type, exerciseResponse.getType());
        assertEquals(starterCode, exerciseResponse.getStarterCode());
        assertEquals(testCases, exerciseResponse.getTestCases());
        assertEquals(testDateTime, exerciseResponse.getCreatedAt());
        assertEquals(testPropositions, exerciseResponse.getPropositions());
        assertEquals(lessonId, exerciseResponse.getLessonId());
        assertEquals(testLesson, exerciseResponse.getLesson());
    }

    @Test
    void testConstructorAndSettersInteraction() {
        // Given
        ExerciseResponse response = new ExerciseResponse(
            1L, "Original Title", "Original Description", ExerciseType.CODE,
            "Original Code", "Original Tests", testDateTime
        );

        // When - Override values set by constructor
        response.setTitle("Modified Title");
        response.setType(ExerciseType.QCM);
        response.setPropositions(testPropositions);

        // Then
        assertEquals(1L, response.getExerciseId());
        assertEquals("Modified Title", response.getTitle()); // Modified
        assertEquals("Original Description", response.getDescription());
        assertEquals(ExerciseType.QCM, response.getType()); // Modified
        assertEquals("Original Code", response.getStarterCode());
        assertEquals("Original Tests", response.getTestCases());
        assertEquals(testDateTime, response.getCreatedAt());
        assertEquals(testPropositions, response.getPropositions()); // Added
    }
}