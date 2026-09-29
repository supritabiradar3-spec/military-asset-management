package com.military.assetmanagement.service.impl;

import com.military.assetmanagement.dto.AuthResponse;
import com.military.assetmanagement.dto.LoginRequest;
import com.military.assetmanagement.security.CustomUserDetails;
import com.military.assetmanagement.security.JwtService;
import com.military.assetmanagement.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final com.military.assetmanagement.service.AuditLogService auditLogService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            com.military.assetmanagement.service.AuditLogService auditLogService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.auditLogService = auditLogService;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails);

            log.info("User '{}' successfully authenticated with role '{}'",
                    userDetails.getUsername(),
                    userDetails.getRoleName());

            auditLogService.logAction(
                    userDetails.getUsername(),
                    "LOGIN",
                    "USER",
                    userDetails.getId(),
                    userDetails.getBaseId(),
                    "User authenticated successfully"
            );

            return AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .username(userDetails.getUsername())
                    .role(userDetails.getRoleName().name())
                    .baseId(userDetails.getBaseId())
                    .build();

        } catch (DisabledException e) {
            log.warn("Authentication failed: User account is disabled");
            throw new BadCredentialsException("Invalid username or password");
        } catch (AuthenticationException e) {
            log.warn("Authentication failed: Invalid credentials provided");
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
