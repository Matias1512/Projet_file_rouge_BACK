package com.learncode.schoolDev.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserBadgeCreateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUserBadgeCreateRequest() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest(1L, 2L);
        
        Set<ConstraintViolation<UserBadgeCreateRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
        assertEquals(1L, request.getUserId());
        assertEquals(2L, request.getBadgeId());
        assertEquals(0, request.getCurrent());
        assertFalse(request.getUnlocked());
    }

    @Test
    void testValidUserBadgeCreateRequestWithAllFields() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest(1L, 2L, 5, true);
        
        Set<ConstraintViolation<UserBadgeCreateRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
        assertEquals(1L, request.getUserId());
        assertEquals(2L, request.getBadgeId());
        assertEquals(5, request.getCurrent());
        assertTrue(request.getUnlocked());
    }

    @Test
    void testDefaultConstructor() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest();
        
        assertNull(request.getUserId());
        assertNull(request.getBadgeId());
        assertEquals(0, request.getCurrent());
        assertFalse(request.getUnlocked());
    }

    @Test
    void testNullUserIdValidation() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest();
        request.setUserId(null);
        request.setBadgeId(1L);
        
        Set<ConstraintViolation<UserBadgeCreateRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("L'ID de l'utilisateur est obligatoire")));
    }

    @Test
    void testNullBadgeIdValidation() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest();
        request.setUserId(1L);
        request.setBadgeId(null);
        
        Set<ConstraintViolation<UserBadgeCreateRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("L'ID du badge est obligatoire")));
    }

    @Test
    void testNegativeUserIdValidation() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest();
        request.setUserId(-1L);
        request.setBadgeId(1L);
        
        Set<ConstraintViolation<UserBadgeCreateRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("L'ID de l'utilisateur doit être positif")));
    }

    @Test
    void testNegativeBadgeIdValidation() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest();
        request.setUserId(1L);
        request.setBadgeId(-1L);
        
        Set<ConstraintViolation<UserBadgeCreateRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("L'ID du badge doit être positif")));
    }

    @Test
    void testZeroUserIdValidation() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest();
        request.setUserId(0L);
        request.setBadgeId(1L);
        
        Set<ConstraintViolation<UserBadgeCreateRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("L'ID de l'utilisateur doit être positif")));
    }

    @Test
    void testSettersAndGetters() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest();
        
        request.setUserId(10L);
        request.setBadgeId(20L);
        request.setCurrent(15);
        request.setUnlocked(true);
        
        assertEquals(10L, request.getUserId());
        assertEquals(20L, request.getBadgeId());
        assertEquals(15, request.getCurrent());
        assertTrue(request.getUnlocked());
    }

    @Test
    void testNullCurrentValue() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest(1L, 2L);
        request.setCurrent(null);
        
        Set<ConstraintViolation<UserBadgeCreateRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
        assertNull(request.getCurrent());
    }

    @Test
    void testNullUnlockedValue() {
        UserBadgeCreateRequest request = new UserBadgeCreateRequest(1L, 2L);
        request.setUnlocked(null);
        
        Set<ConstraintViolation<UserBadgeCreateRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
        assertNull(request.getUnlocked());
    }
}