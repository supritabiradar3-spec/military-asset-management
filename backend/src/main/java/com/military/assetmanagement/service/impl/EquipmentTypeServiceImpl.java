package com.military.assetmanagement.service.impl;

import com.military.assetmanagement.dto.EquipmentTypeRequest;
import com.military.assetmanagement.dto.EquipmentTypeResponse;
import com.military.assetmanagement.entity.EquipmentType;
import com.military.assetmanagement.exception.InvalidOperationException;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.EquipmentTypeRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.EquipmentTypeService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentTypeServiceImpl implements EquipmentTypeService {

    private final EquipmentTypeRepository equipmentTypeRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;

    public EquipmentTypeServiceImpl(
            EquipmentTypeRepository equipmentTypeRepository,
            SecurityUtils securityUtils,
            AuditLogService auditLogService
    ) {
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.securityUtils = securityUtils;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentTypeResponse> getAllEquipmentTypes() {
        return equipmentTypeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentTypeResponse getEquipmentTypeById(Long id) {
        EquipmentType equipmentType = getEquipmentTypeEntity(id);
        return mapToResponse(equipmentType);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentType getEquipmentTypeEntity(Long id) {
        return equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment Type not found with ID: " + id));
    }

    @Override
    @Transactional
    public EquipmentTypeResponse createEquipmentType(EquipmentTypeRequest request) {
        if (!securityUtils.isAdmin()) {
            throw new AccessDeniedException("Only ADMIN users can create equipment types");
        }

        if (equipmentTypeRepository.existsByName(request.getName())) {
            throw new InvalidOperationException("Equipment type with name '" + request.getName() + "' already exists");
        }

        EquipmentType equipmentType = EquipmentType.builder()
                .name(request.getName().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .build();

        EquipmentType saved = equipmentTypeRepository.save(equipmentType);

        auditLogService.logAction(
                securityUtils.getCurrentUsername(),
                "EQUIPMENT_TYPE_CREATED",
                "EQUIPMENT_TYPE",
                saved.getId(),
                null,
                "Created equipment type: " + saved.getName()
        );

        return mapToResponse(saved);
    }

    private EquipmentTypeResponse mapToResponse(EquipmentType type) {
        return EquipmentTypeResponse.builder()
                .id(type.getId())
                .name(type.getName())
                .description(type.getDescription())
                .build();
    }
}
