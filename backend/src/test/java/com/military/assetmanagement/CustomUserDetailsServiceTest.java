package com.military.assetmanagement;

import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.RoleName;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.security.CustomUserDetails;
import com.military.assetmanagement.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = Role.builder()
                .id(1L)
                .name(RoleName.ADMIN)
                .build();

        Base base = Base.builder()
                .id(10L)
                .name("Alpha Forward Base")
                .location("Sector 4")
                .build();

        testUser = User.builder()
                .id(1L)
                .username("test_admin")
                .email("admin@test.mil")
                .password("$2a$10$hashedpasswordstring")
                .role(role)
                .base(base)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Should load user by username and map role authorities")
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByUsername("test_admin")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test_admin");

        assertNotNull(userDetails);
        assertEquals("test_admin", userDetails.getUsername());
        assertEquals("$2a$10$hashedpasswordstring", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        assertInstanceOf(CustomUserDetails.class, userDetails);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        assertEquals(RoleName.ADMIN, customUserDetails.getRoleName());
        assertEquals(10L, customUserDetails.getBaseId());
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("unknown_user")
        );

        verify(userRepository, times(1)).findByUsername("unknown_user");
    }
}
