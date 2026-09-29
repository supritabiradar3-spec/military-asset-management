package com.military.assetmanagement;

import com.military.assetmanagement.entity.RoleName;
import com.military.assetmanagement.security.CustomUserDetails;
import com.military.assetmanagement.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // 256-bit base64 encoded secret for testing
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "c2VjdXJlX21pbGl0YXJ5X2Fzc2V0X21hbmFnZW1lbnRfc3lzdGVtX2tleV8yMDI2X2FneV9zZWN1cmVfc2VjcmV0X2tleQ==");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L); // 1 hour
    }

    @Test
    @DisplayName("Should generate valid JWT token for CustomUserDetails")
    void testGenerateAndValidateToken() {
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(1L)
                .username("admin_user")
                .email("admin@military.gov")
                .password("encoded_pass")
                .roleName(RoleName.ADMIN)
                .baseId(10L)
                .enabled(true)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));

        String username = jwtService.getUsernameFromToken(token);
        assertEquals("admin_user", username);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    @DisplayName("Should extract custom claims correctly from token")
    void testExtractClaims() {
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(5L)
                .username("commander_john")
                .email("john@military.gov")
                .password("encoded_pass")
                .roleName(RoleName.BASE_COMMANDER)
                .baseId(42L)
                .enabled(true)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_BASE_COMMANDER")))
                .build();

        String token = jwtService.generateToken(userDetails);

        assertEquals("commander_john", jwtService.getUsernameFromToken(token));
        assertEquals("BASE_COMMANDER", jwtService.extractClaim(token, claims -> claims.get("role", String.class)));
        Number baseId = jwtService.extractClaim(token, claims -> claims.get("baseId", Number.class));
        assertNotNull(baseId);
        assertEquals(42L, baseId.longValue());
        Number userId = jwtService.extractClaim(token, claims -> claims.get("userId", Number.class));
        assertNotNull(userId);
        assertEquals(5L, userId.longValue());
    }

    @Test
    @DisplayName("Should reject invalid or malformed tokens")
    void testInvalidToken() {
        assertFalse(jwtService.validateToken("invalid.malformed.token"));
        assertFalse(jwtService.validateToken(""));
        assertFalse(jwtService.validateToken(null));
    }

    @Test
    @DisplayName("Should reject expired token")
    void testExpiredToken() {
        // Set expiration to negative value (already expired)
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1000L);

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(2L)
                .username("logistics_user")
                .email("logistics@military.gov")
                .password("encoded_pass")
                .roleName(RoleName.LOGISTICS_OFFICER)
                .enabled(true)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_LOGISTICS_OFFICER")))
                .build();

        String expiredToken = jwtService.generateToken(userDetails);
        assertFalse(jwtService.validateToken(expiredToken));
    }
}
