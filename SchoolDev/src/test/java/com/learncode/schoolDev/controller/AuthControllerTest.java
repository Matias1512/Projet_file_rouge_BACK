package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.config.JwtUtils;
import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.repository.BadgeRepository;
import com.learncode.schoolDev.repository.UserBadgeRepository;
import com.learncode.schoolDev.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private BadgeRepository badgeRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("password");
    }

    @Test
    void testRegisterFailsWhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.register(user);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    void testRegisterSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(1L);
            return u;
        });

        List<Badge> badges = List.of(new Badge(), new Badge());
        when(badgeRepository.findAll()).thenReturn(badges);

        ResponseEntity<?> response = authController.register(user);

        assertEquals(200, response.getStatusCode().value());
        User savedUser = (User) response.getBody();
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());

        verify(userBadgeRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testLoginSuccess() {
        Map<String, String> loginData = Map.of("username", "testuser", "password", "password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateToken("testuser")).thenReturn("token123");

        ResponseEntity<?> response = authController.login(loginData);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof Map);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("token123", body.get("token"));
        assertEquals("Bearer", body.get("type"));
    }

    @Test
    void testLoginFailure() {
        Map<String, String> loginData = Map.of("username", "testuser", "password", "wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        ResponseEntity<?> response = authController.login(loginData);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    void testLogin_Unauthorized_whenAuthenticationNotAuthenticated() {
        // Arrange
        Map<String, String> loginData = Map.of("username", "testuser", "password", "wrong");

        // Mock du retour de authenticationManager.authenticate(...) : ne lève pas d'exception
        Authentication fakeAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(fakeAuthentication);
        // ... mais isAuthenticated() retourne false !
        when(fakeAuthentication.isAuthenticated()).thenReturn(false);

        // Act
        ResponseEntity<?> response = authController.login(loginData);

        // Assert
        assertEquals(401, response.getStatusCode().value());
        assertEquals("Invalid username or password", response.getBody());
    }
}
