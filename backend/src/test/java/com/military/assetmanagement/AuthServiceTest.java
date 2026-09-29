package com.military.assetmanagement;

import com.military.assetmanagement.dto.AuthResponse;
import com.military.assetmanagement.dto.LoginRequest;
import com.military.assetmanagement.entity.RoleName;
import com.military.assetmanagement.security.CustomUserDetails;
import com.military.assetmanagement.security.JwtService;
import com.military.assetmanagement.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private com.military.assetmanagement.service.AuditLogService auditLogService;

    @InjectMocks
    private AuthServiceImpl authService;

    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        customUserDetails = CustomUserDetails.builder()
                .id(100L)
                .username("commander1")
                .email("commander@test.mil")
                .password("encodedPassword")
                .roleName(RoleName.BASE_COMMANDER)
                .baseId(25L)
                .enabled(true)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_BASE_COMMANDER")))
                .build();
    }

    @Test
    @DisplayName("Should successfully authenticate and return AuthResponse on valid credentials")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("commander1", "validPassword");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(customUserDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtService.generateToken(customUserDetails)).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("commander1", response.getUsername());
        assertEquals("BASE_COMMANDER", response.getRole());
        assertEquals(25L, response.getBaseId());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid credentials without leaking specific details")
    void testLoginInvalidCredentials() {
        LoginRequest request = new LoginRequest("commander1", "wrongPassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                authService.login(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when account is disabled")
    void testLoginDisabledAccount() {
        LoginRequest request = new LoginRequest("disabledUser", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("User is disabled"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                authService.login(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());
    }
}
