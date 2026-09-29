package com.military.assetmanagement.security;

import com.military.assetmanagement.entity.RoleName;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new AccessDeniedException("Invalid user session");
    }

    public String getCurrentUsername() {
        return getCurrentUserDetails().getUsername();
    }

    public Long getCurrentUserId() {
        return getCurrentUserDetails().getId();
    }

    public RoleName getCurrentUserRole() {
        return getCurrentUserDetails().getRoleName();
    }

    public Long getCurrentUserBaseId() {
        return getCurrentUserDetails().getBaseId();
    }

    public boolean isAdmin() {
        return RoleName.ADMIN.equals(getCurrentUserRole());
    }

    public boolean isBaseCommander() {
        return RoleName.BASE_COMMANDER.equals(getCurrentUserRole());
    }

    public boolean isLogisticsOfficer() {
        return RoleName.LOGISTICS_OFFICER.equals(getCurrentUserRole());
    }

    public void validateBaseAccess(Long targetBaseId) {
        if (isAdmin()) {
            return;
        }
        Long userBaseId = getCurrentUserBaseId();
        if (userBaseId == null) {
            throw new AccessDeniedException("Access denied: No base assigned to your account");
        }
        if (targetBaseId == null || !userBaseId.equals(targetBaseId)) {
            throw new AccessDeniedException("Access denied: You are not authorized to perform operations for base ID " + targetBaseId);
        }
    }

    public Long resolveAccessibleBaseId(Long requestedBaseId) {
        if (isAdmin()) {
            return requestedBaseId;
        }
        Long userBaseId = getCurrentUserBaseId();
        if (userBaseId == null) {
            throw new AccessDeniedException("Access denied: No base assigned to your account");
        }
        if (requestedBaseId != null && !requestedBaseId.equals(userBaseId)) {
            throw new AccessDeniedException("Access denied: You cannot view data for base ID " + requestedBaseId);
        }
        return userBaseId;
    }
}
