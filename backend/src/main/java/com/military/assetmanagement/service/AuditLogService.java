package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.AuditLogResponse;
import com.military.assetmanagement.entity.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {
    AuditLog logAction(String username, String action, String entityType, Long entityId, Long baseId, String details);
    List<AuditLogResponse> getAuditLogs(Long baseId, String action, String entityType, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
