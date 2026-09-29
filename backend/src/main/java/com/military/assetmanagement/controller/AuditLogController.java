package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.AuditLogResponse;
import com.military.assetmanagement.service.AuditLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime
    ) {
        List<AuditLogResponse> logs = auditLogService.getAuditLogs(baseId, action, entityType, startDateTime, endDateTime);
        return ResponseEntity.ok(logs);
    }
}
