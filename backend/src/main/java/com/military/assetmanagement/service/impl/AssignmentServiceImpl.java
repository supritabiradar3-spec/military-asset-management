package com.military.assetmanagement.service.impl;

import com.military.assetmanagement.dto.AssignmentRequest;
import com.military.assetmanagement.dto.AssignmentResponse;
import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Assignment;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.InsufficientInventoryException;
import com.military.assetmanagement.exception.InvalidOperationException;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.AssignmentRepository;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AssetService;
import com.military.assetmanagement.service.AssignmentService;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private static final Logger log = LoggerFactory.getLogger(AssignmentServiceImpl.class);

    private final AssignmentRepository assignmentRepository;
    private final AssetRepository assetRepository;
    private final BaseService baseService;
    private final AssetService assetService;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;

    public AssignmentServiceImpl(
            AssignmentRepository assignmentRepository,
            AssetRepository assetRepository,
            BaseService baseService,
            AssetService assetService,
            UserRepository userRepository,
            SecurityUtils securityUtils,
            AuditLogService auditLogService
    ) {
        this.assignmentRepository = assignmentRepository;
        this.assetRepository = assetRepository;
        this.baseService = baseService;
        this.assetService = assetService;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        if (securityUtils.isLogisticsOfficer()) {
            throw new AccessDeniedException("LOGISTICS_OFFICER is not authorized to create equipment assignments");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new InvalidOperationException("Assignment quantity must be positive and greater than zero");
        }

        securityUtils.validateBaseAccess(request.getBaseId());

        Base base = baseService.getBaseEntity(request.getBaseId());
        Asset asset = assetService.getAssetEntity(request.getAssetId());

        if (!asset.getBase().getId().equals(base.getId())) {
            throw new InvalidOperationException(
                    String.format("Asset '%s' does not belong to base '%s'", asset.getName(), base.getName())
            );
        }

        // Validate available inventory (unassigned stock)
        if (asset.getAvailableQuantity() < request.getQuantity()) {
            throw new InsufficientInventoryException(
                    String.format("Insufficient available inventory for asset '%s' at base '%s'. Available: %d, Requested: %d",
                            asset.getName(), base.getName(), asset.getAvailableQuantity(), request.getQuantity())
            );
        }

        // Increase assigned quantity (reduces available quantity without removing from base inventory)
        int currentAssigned = asset.getAssignedQuantity() != null ? asset.getAssignedQuantity() : 0;
        asset.setAssignedQuantity(currentAssigned + request.getQuantity());
        assetRepository.save(asset);

        User currentUser = userRepository.findById(securityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        Assignment assignment = Assignment.builder()
                .asset(asset)
                .base(base)
                .personnelName(request.getPersonnelName().trim())
                .personnelIdentifier(request.getPersonnelIdentifier() != null ? request.getPersonnelIdentifier().trim() : null)
                .quantity(request.getQuantity())
                .assignmentDate(request.getAssignmentDate() != null ? request.getAssignmentDate() : LocalDate.now())
                .status("ACTIVE")
                .notes(request.getNotes() != null ? request.getNotes().trim() : null)
                .createdBy(currentUser)
                .build();

        Assignment saved = assignmentRepository.save(assignment);

        log.info("Assignment recorded: ID={}, Base={}, Personnel={}, Asset={}, Qty={}",
                saved.getId(), base.getName(), saved.getPersonnelName(), asset.getName(), saved.getQuantity());

        auditLogService.logAction(
                currentUser.getUsername(),
                "ASSIGNMENT_CREATED",
                "ASSIGNMENT",
                saved.getId(),
                base.getId(),
                String.format("Assigned %d units of '%s' to '%s' at base '%s'",
                        saved.getQuantity(), asset.getName(), saved.getPersonnelName(), base.getName())
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentResponse getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + id));

        securityUtils.validateBaseAccess(assignment.getBase().getId());

        return mapToResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignments(
            Long baseId,
            Long equipmentTypeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Long resolvedBaseId = securityUtils.resolveAccessibleBaseId(baseId);

        return assignmentRepository.findFiltered(resolvedBaseId, equipmentTypeId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AssignmentResponse mapToResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .assetId(assignment.getAsset().getId())
                .assetName(assignment.getAsset().getName())
                .equipmentTypeId(assignment.getAsset().getEquipmentType().getId())
                .equipmentTypeName(assignment.getAsset().getEquipmentType().getName())
                .baseId(assignment.getBase().getId())
                .baseName(assignment.getBase().getName())
                .personnelName(assignment.getPersonnelName())
                .personnelIdentifier(assignment.getPersonnelIdentifier())
                .quantity(assignment.getQuantity())
                .assignmentDate(assignment.getAssignmentDate())
                .status(assignment.getStatus())
                .notes(assignment.getNotes())
                .createdByUsername(assignment.getCreatedBy().getUsername())
                .createdAt(assignment.getCreatedAt())
                .build();
    }
}
