package com.learncode.schoolDev.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @Mock
    private JwtSecretReader jwtSecretReader;

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(jwtSecretReader);
        // Définir une expiration de 1 heure pour les tests
        ReflectionTestUtils.setField(jwtUtils, "jwtExpiration", 3600000L);
        
        // Mock pour retourner une clé secrète de test (256 bits minimum)
        when(jwtSecretReader.getJwtSecretKey()).thenReturn("mySecretKey123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789mySecretKey123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }

    @Test
    void testGenerateToken() {
        String username = "testuser";
        
        String token = jwtUtils.generateToken(username);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void testExtractUsername() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        String extractedUsername = jwtUtils.extractUsername(token);
        
        assertEquals(username, extractedUsername);
    }

    @Test
    void testIsTokenValid_ValidToken() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        UserDetails userDetails = new User(username, "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        
        boolean isValid = jwtUtils.isTokenValid(token, userDetails);
        
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_WrongUsername() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        UserDetails userDetails = new User("wronguser", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        
        boolean isValid = jwtUtils.isTokenValid(token, userDetails);
        
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_ExpiredToken() {
        // Créer un token avec une expiration négative (déjà expiré)
        ReflectionTestUtils.setField(jwtUtils, "jwtExpiration", -1000L); // Token expiré
        
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        UserDetails userDetails = new User(username, "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        
        // Vérifier que le token est expiré en catchant l'exception
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtils.isTokenValid(token, userDetails);
        });
    }

    @Test
    void testExtractUsername_InvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertThrows(MalformedJwtException.class, () -> {
            jwtUtils.extractUsername(invalidToken);
        });
    }

    @Test
    void testIsTokenValid_InvalidSignature() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        // Modifier la clé secrète pour invalider la signature (doit être assez longue)
        when(jwtSecretReader.getJwtSecretKey()).thenReturn("differentSecretKey12345differentSecretKey12345differentSecretKey12345differentSecretKey12345differentSecretKey12345differentSecretKey12345");
        
        UserDetails userDetails = new User(username, "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        
        // Peut lever SignatureException ou WeakKeyException selon la version
        assertThrows(RuntimeException.class, () -> {
            jwtUtils.isTokenValid(token, userDetails);
        });
    }

    @Test
    void testCreateToken_WithClaims() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        // Vérifier que le token contient les claims attendus
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length); // JWT doit avoir 3 parties
        
        String extractedUsername = jwtUtils.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testTokenExpiration() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        // Extraire la date d'expiration en utilisant la réflexion pour accéder à la méthode privée
        Date expirationDate = extractExpirationDateUsingReflection(token);
        
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testTokenIssuedAt() {
        String username = "testuser";
        long beforeGeneration = System.currentTimeMillis();
        String token = jwtUtils.generateToken(username);
        long afterGeneration = System.currentTimeMillis();
        
        // Vérifier que le token a été généré récemment
        Claims claims = extractAllClaimsUsingReflection(token);
        Date issuedAt = claims.getIssuedAt();
        
        assertNotNull(issuedAt);
        long issuedAtTime = issuedAt.getTime();
        assertTrue(issuedAtTime >= beforeGeneration - 1000); // Tolerance de 1 seconde
        assertTrue(issuedAtTime <= afterGeneration + 1000); // Tolerance de 1 seconde
    }

    @Test
    void testDifferentUsernamesGenerateDifferentTokens() {
        String username1 = "user1";
        String username2 = "user2";
        
        String token1 = jwtUtils.generateToken(username1);
        String token2 = jwtUtils.generateToken(username2);
        
        assertNotEquals(token1, token2);
        assertEquals(username1, jwtUtils.extractUsername(token1));
        assertEquals(username2, jwtUtils.extractUsername(token2));
    }

    @Test
    void testGetSignInKeyMethod() throws Exception {
        // Tester la méthode getSignInKey indirectement via plusieurs générations de tokens
        String[] usernames = {"user1", "user2", "admin", "test@email.com"};
        
        for (String username : usernames) {
            String token = jwtUtils.generateToken(username);
            
            // Vérifier que chaque token peut être décodé (donc la clé fonctionne correctement)
            assertNotNull(token);
            assertTrue(token.length() > 50); // JWT tokens are typically longer
            
            // Vérifier la structure JWT (header.payload.signature)
            String[] tokenParts = token.split("\\.");
            assertEquals(3, tokenParts.length);
            
            // Chaque partie ne doit pas être vide
            for (String part : tokenParts) {
                assertFalse(part.isEmpty());
            }
        }
    }

    @Test
    void testCreateTokenMethod() {
        // Tester createToken indirectement via generateToken
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        assertNotNull(token);
        assertTrue(token.contains("."));
        assertEquals(3, token.split("\\.").length);
        
        // Vérifier que le token contient le bon username
        assertEquals(username, jwtUtils.extractUsername(token));
    }

    @Test
    void testIsTokenExpiredMethod() {
        // Test avec un token valide
        String username = "testuser";
        String validToken = jwtUtils.generateToken(username);
        
        // Le token devrait être valide
        UserDetails userDetails = new User(username, "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(jwtUtils.isTokenValid(validToken, userDetails));
        
        // Test avec un token expiré
        ReflectionTestUtils.setField(jwtUtils, "jwtExpiration", -1000L);
        String expiredToken = jwtUtils.generateToken(username);
        
        // Le token expiré devrait être invalide
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtils.isTokenValid(expiredToken, userDetails);
        });
    }

    @Test
    void testExtractClaimMethod() {
        String username = "claimTestUser";
        String token = jwtUtils.generateToken(username);
        
        // Tester extractClaim via plusieurs types de claims
        Claims allClaims = extractAllClaimsUsingReflection(token);
        
        // Vérifier le subject (username)
        assertNotNull(allClaims.getSubject());
        assertEquals(username, allClaims.getSubject());
        
        // Vérifier issued at
        assertNotNull(allClaims.getIssuedAt());
        assertTrue(allClaims.getIssuedAt().before(new Date(System.currentTimeMillis() + 1000)));
        
        // Vérifier expiration
        assertNotNull(allClaims.getExpiration());
        assertTrue(allClaims.getExpiration().after(new Date()));
        
        // Vérifier que issued at est avant expiration
        assertTrue(allClaims.getIssuedAt().before(allClaims.getExpiration()));
    }

    // Méthodes utilitaires pour accéder aux méthodes privées via réflexion
    private Date extractExpirationDateUsingReflection(String token) {
        try {
            return ReflectionTestUtils.invokeMethod(jwtUtils, "extractExpirationDate", token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Claims extractAllClaimsUsingReflection(String token) {
        try {
            return ReflectionTestUtils.invokeMethod(jwtUtils, "extractAllClaims", token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}