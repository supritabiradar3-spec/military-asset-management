package com.military.assetmanagement.service.impl;

import com.military.assetmanagement.dto.AuditLogResponse;
import com.military.assetmanagement.entity.AuditLog;
import com.military.assetmanagement.repository.AuditLogRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogRepository auditLogRepository;
    private final SecurityUtils securityUtils;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, SecurityUtils securityUtils) {
        this.auditLogRepository = auditLogRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog logAction(
            String username,
            String action,
            String entityType,
            Long entityId,
            Long baseId,
            String details
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .username(username != null ? username : "SYSTEM")
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .baseId(baseId)
                    .details(details)
                    .timestamp(LocalDateTime.now())
                    .build();

            AuditLog saved = auditLogRepository.save(auditLog);
            log.info("Audit log recorded: action={}, user={}, entityType={}, entityId={}", action, username, entityType, entityId);
            return saved;
        } catch (Exception e) {
            log.error("Failed to record audit log: action={}, error={}", action, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogs(
            Long baseId,
            String action,
            String entityType,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        Long resolvedBaseId = securityUtils.resolveAccessibleBaseId(baseId);

        return auditLogRepository.findFiltered(resolvedBaseId, action, entityType, startDateTime, endDateTime)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .username(auditLog.getUsername())
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .baseId(auditLog.getBaseId())
                .details(auditLog.getDetails())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
