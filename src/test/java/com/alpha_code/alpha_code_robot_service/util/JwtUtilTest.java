package com.alpha_code.alpha_code_robot_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String jwtSecret;
    private int jwtExpirationMs;
    private int refreshTokenExpirationMs;
    private int resetPasswordTokenExpirationMs;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        jwtSecret = "testSecretKeyForJwtTokenGeneration12345678901234567890";
        jwtExpirationMs = 3600000; // 1 hour
        refreshTokenExpirationMs = 86400000; // 24 hours
        resetPasswordTokenExpirationMs = 3600000; // 1 hour

        // Set private fields using reflection
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", jwtExpirationMs);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpirationMs", refreshTokenExpirationMs);
        ReflectionTestUtils.setField(jwtUtil, "resetPasswordTokenExpirationMs", resetPasswordTokenExpirationMs);

        signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should get refresh token expiration ms")
    void testGetRefreshTokenExpirationMs() {
        // When
        Integer result = jwtUtil.getRefreshTokenExpirationMs();

        // Then
        assertNotNull(result);
        assertEquals(refreshTokenExpirationMs, result);
    }

    @Test
    @DisplayName("Should get all claims from valid token")
    void testGetAllClaims_ValidToken() {
        // Given
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String token = generateTestToken(username, userId, email);

        // When
        Claims claims = jwtUtil.getAllClaims(token);

        // Then
        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertEquals(userId.toString(), claims.get("id", String.class));
        assertEquals(email, claims.get("email", String.class));
    }

    @Test
    @DisplayName("Should get username from JWT token")
    void testGetUsernameFromJwt() {
        // Given
        String username = "testuser";
        String token = generateTestToken(username, UUID.randomUUID(), "test@example.com");

        // When
        String result = jwtUtil.getUsernameFromJwt(token);

        // Then
        assertNotNull(result);
        assertEquals(username, result);
    }

    @Test
    @DisplayName("Should get user ID from token")
    void testGetUserIdFromToken() {
        // Given
        UUID userId = UUID.randomUUID();
        String token = generateTestToken("testuser", userId, "test@example.com");

        // When
        UUID result = jwtUtil.getUserIdFromToken(token);

        // Then
        assertNotNull(result);
        assertEquals(userId, result);
    }

    @Test
    @DisplayName("Should validate valid JWT token")
    void testValidateJwtToken_ValidToken() {
        // Given
        String token = generateTestToken("testuser", UUID.randomUUID(), "test@example.com");

        // When
        boolean result = jwtUtil.validateJwtToken(token);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false for invalid JWT token")
    void testValidateJwtToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean result = jwtUtil.validateJwtToken(invalidToken);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false for null token")
    void testValidateJwtToken_NullToken() {
        // When
        boolean result = jwtUtil.validateJwtToken(null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false for empty token")
    void testValidateJwtToken_EmptyToken() {
        // When
        boolean result = jwtUtil.validateJwtToken("");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should extract claim from token")
    void testExtractClaim() {
        // Given
        String username = "testuser";
        String token = generateTestToken(username, UUID.randomUUID(), "test@example.com");

        // When
        String result = jwtUtil.extractClaim(token, Claims::getSubject);

        // Then
        assertNotNull(result);
        assertEquals(username, result);
    }

    @Test
    @DisplayName("Should extract email from token")
    void testExtractEmail() {
        // Given
        String email = "test@example.com";
        String token = generateTestToken("testuser", UUID.randomUUID(), email);

        // When
        String result = jwtUtil.extractEmail(token);

        // Then
        assertNotNull(result);
        assertEquals(email, result);
    }

    @Test
    @DisplayName("Should throw exception for expired token")
    void testValidateJwtToken_ExpiredToken() {
        // Given
        String expiredToken = generateExpiredToken("testuser", UUID.randomUUID(), "test@example.com");

        // When
        boolean result = jwtUtil.validateJwtToken(expiredToken);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should throw exception when getting claims from invalid token")
    void testGetAllClaims_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtUtil.getAllClaims(invalidToken));
    }

    /**
     * Helper method to generate a test JWT token
     */
    private String generateTestToken(String username, UUID userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId.toString());
        claims.put("email", email);

        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Helper method to generate an expired JWT token
     */
    private String generateExpiredToken(String username, UUID userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - TimeUnit.HOURS.toMillis(1)); // Expired 1 hour ago

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId.toString());
        claims.put("email", email);

        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }
}

