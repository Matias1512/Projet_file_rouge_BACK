package com.learncode.schoolDev.filter;

import com.learncode.schoolDev.config.RateLimitingConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    @Mock
    private RateLimitingConfig rateLimitingConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Bucket bucket;

    @Mock
    private ConsumptionProbe probe;

    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void setUp() {
        rateLimitingFilter = new RateLimitingFilter(rateLimitingConfig);
    }

    @Test
    void testDoFilterInternal_WhenRequestAllowed() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimitingConfig.resolveBucket("general:127.0.0.1")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(99L);

        // Act
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).addHeader("X-Rate-Limit-Remaining", "99");
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }

    @Test
    void testDoFilterInternal_WhenRequestBlocked() throws Exception {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimitingConfig.resolveBucket("general:127.0.0.1")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(false);
        when(probe.getNanosToWaitForRefill()).thenReturn(60_000_000_000L); // 60 seconds
        when(response.getWriter()).thenReturn(writer);

        // Act
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).addHeader("X-Rate-Limit-Retry-After-Seconds", "60");
        verify(response).setStatus(429);
        verify(filterChain, never()).doFilter(request, response);
        
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("Too many requests"));
        assertTrue(responseBody.contains("retry_after"));
    }

    @Test
    void testDoFilterInternal_AuthEndpoint() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(rateLimitingConfig.resolveBucket("auth:192.168.1.1")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(4L);

        // Act
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(rateLimitingConfig).resolveBucket("auth:192.168.1.1");
        verify(response).addHeader("X-Rate-Limit-Remaining", "4");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testGetClientIP_WithXForwardedFor() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.195, 70.41.3.18, 150.172.238.178");
        when(rateLimitingConfig.resolveBucket("general:203.0.113.195")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(50L);

        // Act
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(rateLimitingConfig).resolveBucket("general:203.0.113.195");
    }

    @Test
    void testGetClientIP_WithXRealIP() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn("10.0.0.1");
        when(rateLimitingConfig.resolveBucket("general:10.0.0.1")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(75L);

        // Act
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(rateLimitingConfig).resolveBucket("general:10.0.0.1");
    }

    @Test
    void testGetClientIP_WithEmptyHeaders() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getHeader("X-Real-IP")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("172.16.0.1");
        when(rateLimitingConfig.resolveBucket("general:172.16.0.1")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(80L);

        // Act
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(rateLimitingConfig).resolveBucket("general:172.16.0.1");
    }

    @Test
    void testGetClientIP_FallbackToRemoteAddr() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.0.100");
        when(rateLimitingConfig.resolveBucket("general:192.168.0.100")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(true);
        when(probe.getRemainingTokens()).thenReturn(90L);

        // Act
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(rateLimitingConfig).resolveBucket("general:192.168.0.100");
    }
}