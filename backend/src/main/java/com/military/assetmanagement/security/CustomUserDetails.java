package com.military.assetmanagement.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.military.assetmanagement.entity.RoleName;
import com.military.assetmanagement.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CustomUserDetails implements UserDetails {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private String password;
        private RoleName roleName;
        private Long baseId;
        private boolean enabled;
        private Collection<? extends GrantedAuthority> authorities;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder roleName(RoleName roleName) {
            this.roleName = roleName;
            return this;
        }

        public Builder baseId(Long baseId) {
            this.baseId = baseId;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public CustomUserDetails build() {
            return new CustomUserDetails(id, username, email, password, roleName, baseId, enabled, authorities);
        }
    }

    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String username;
    private final String email;

    @JsonIgnore
    private final String password;

    private final RoleName roleName;
    private final Long baseId;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(
            Long id,
            String username,
            String email,
            String password,
            RoleName roleName,
            Long baseId,
            boolean enabled,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roleName = roleName;
        this.baseId = baseId;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static CustomUserDetails build(User user) {
        RoleName roleName = user.getRole().getName();
        // Map RoleName to Spring Security authority with standard ROLE_ prefix and direct name
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + roleName.name()),
                new SimpleGrantedAuthority(roleName.name())
        );

        Long baseId = user.getBase() != null ? user.getBase().getId() : null;

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .roleName(roleName)
                .baseId(baseId)
                .enabled(user.isEnabled())
                .authorities(authorities)
                .build();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public Long getBaseId() {
        return baseId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
