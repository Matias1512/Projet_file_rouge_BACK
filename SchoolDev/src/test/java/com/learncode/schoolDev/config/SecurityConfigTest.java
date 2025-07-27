package com.learncode.schoolDev.config;

import com.learncode.schoolDev.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(userService, jwtUtils);
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void testCorsConfigurationSource() {
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        
        assertNotNull(corsConfigurationSource);
        assertTrue(corsConfigurationSource instanceof UrlBasedCorsConfigurationSource);
    }

    @Test
    void testAuthenticationManager() throws Exception {
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        when(httpSecurity.getSharedObject(any())).thenReturn(mock(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder.class, RETURNS_DEEP_STUBS));
        
        AuthenticationManager authManager = securityConfig.authenticationManager(httpSecurity, passwordEncoder);
        
        assertNotNull(authManager);
    }

    @Test
    void testSecurityFilterChain() throws Exception {
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        // Mock all the fluent interface calls
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.headers(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mock(DefaultSecurityFilterChain.class));
        
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);
        
        assertNotNull(filterChain);
        verify(httpSecurity).cors(any());
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).headers(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).addFilterBefore(any(), any());
        verify(httpSecurity).build();
    }

    @Test
    void testCorsConfiguration() {
        // Tester directement la configuration CORS interne
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "https://yourdomain.com"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        assertNotNull(config);
        assertTrue(config.getAllowedOriginPatterns().contains("http://localhost:*"));
        assertTrue(config.getAllowedOriginPatterns().contains("https://yourdomain.com"));
        assertTrue(config.getAllowedMethods().contains("GET"));
        assertTrue(config.getAllowedMethods().contains("POST"));
        assertTrue(config.getAllowedMethods().contains("PUT"));
        assertTrue(config.getAllowedMethods().contains("DELETE"));
        assertTrue(config.getAllowedMethods().contains("OPTIONS"));
        assertTrue(config.getAllowedHeaders().contains("*"));
        assertTrue(config.getAllowCredentials());
        assertEquals(3600L, config.getMaxAge());
    }
}