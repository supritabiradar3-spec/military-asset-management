package com.military.assetmanagement.service.impl;

import com.military.assetmanagement.dto.AssetRequest;
import com.military.assetmanagement.dto.AssetResponse;
import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;
import com.military.assetmanagement.exception.InvalidOperationException;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AssetService;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.BaseService;
import com.military.assetmanagement.service.EquipmentTypeService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final BaseService baseService;
    private final EquipmentTypeService equipmentTypeService;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;

    public AssetServiceImpl(
            AssetRepository assetRepository,
            BaseService baseService,
            EquipmentTypeService equipmentTypeService,
            SecurityUtils securityUtils,
            AuditLogService auditLogService
    ) {
        this.assetRepository = assetRepository;
        this.baseService = baseService;
        this.equipmentTypeService = equipmentTypeService;
        this.securityUtils = securityUtils;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponse> getAssets(Long baseId, Long equipmentTypeId) {
        Long resolvedBaseId = securityUtils.resolveAccessibleBaseId(baseId);

        List<Asset> assets;
        if (resolvedBaseId != null && equipmentTypeId != null) {
            assets = assetRepository.findByBaseIdAndEquipmentTypeId(resolvedBaseId, equipmentTypeId);
        } else if (resolvedBaseId != null) {
            assets = assetRepository.findByBaseId(resolvedBaseId);
        } else if (equipmentTypeId != null) {
            assets = assetRepository.findByEquipmentTypeId(equipmentTypeId);
        } else {
            assets = assetRepository.findAll();
        }

        return assets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getAssetById(Long id) {
        Asset asset = getAssetEntity(id);
        securityUtils.validateBaseAccess(asset.getBase().getId());
        return mapToResponse(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public Asset getAssetEntity(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));
    }

    @Override
    @Transactional
    public AssetResponse createAsset(AssetRequest request) {
        if (!securityUtils.isAdmin()) {
            throw new AccessDeniedException("Only ADMIN users can initialize asset master records");
        }

        Base base = baseService.getBaseEntity(request.getBaseId());
        EquipmentType equipmentType = equipmentTypeService.getEquipmentTypeEntity(request.getEquipmentTypeId());

        if (assetRepository.existsByNameAndBaseId(request.getName(), base.getId())) {
            throw new InvalidOperationException("Asset '" + request.getName() + "' already exists at base '" + base.getName() + "'");
        }

        Asset asset = Asset.builder()
                .name(request.getName().trim())
                .equipmentType(equipmentType)
                .base(base)
                .quantity(request.getQuantity() != null ? request.getQuantity() : Integer.valueOf(0))
                .assignedQuantity(0)
                .build();

        Asset saved = assetRepository.save(asset);

        auditLogService.logAction(
                securityUtils.getCurrentUsername(),
                "ASSET_CREATED",
                "ASSET",
                saved.getId(),
                base.getId(),
                "Created asset record: " + saved.getName() + " at base: " + base.getName()
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public Asset findOrCreateAsset(Base base, EquipmentType equipmentType, String name) {
        return assetRepository.findByNameAndBaseId(name, base.getId())
                .orElseGet(() -> {
                    Asset newAsset = Asset.builder()
                            .name(name)
                            .equipmentType(equipmentType)
                            .base(base)
                            .quantity(0)
                            .assignedQuantity(0)
                            .build();
                    return assetRepository.save(newAsset);
                });
    }

    private AssetResponse mapToResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .name(asset.getName())
                .equipmentTypeId(asset.getEquipmentType().getId())
                .equipmentTypeName(asset.getEquipmentType().getName())
                .baseId(asset.getBase().getId())
                .baseName(asset.getBase().getName())
                .quantity(asset.getQuantity())
                .assignedQuantity(asset.getAssignedQuantity())
                .availableQuantity(asset.getAvailableQuantity())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
