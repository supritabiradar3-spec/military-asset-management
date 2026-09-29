package com.military.assetmanagement.service.impl;

import com.military.assetmanagement.dto.PurchaseRequest;
import com.military.assetmanagement.dto.PurchaseResponse;
import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;
import com.military.assetmanagement.entity.Purchase;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.InvalidOperationException;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.PurchaseRepository;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AssetService;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.BaseService;
import com.military.assetmanagement.service.EquipmentTypeService;
import com.military.assetmanagement.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseServiceImpl.class);

    private final PurchaseRepository purchaseRepository;
    private final AssetRepository assetRepository;
    private final BaseService baseService;
    private final EquipmentTypeService equipmentTypeService;
    private final AssetService assetService;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;

    public PurchaseServiceImpl(
            PurchaseRepository purchaseRepository,
            AssetRepository assetRepository,
            BaseService baseService,
            EquipmentTypeService equipmentTypeService,
            AssetService assetService,
            UserRepository userRepository,
            SecurityUtils securityUtils,
            AuditLogService auditLogService
    ) {
        this.purchaseRepository = purchaseRepository;
        this.assetRepository = assetRepository;
        this.baseService = baseService;
        this.equipmentTypeService = equipmentTypeService;
        this.assetService = assetService;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new InvalidOperationException("Purchase quantity must be positive and greater than zero");
        }

        // Enforce base authorization
        securityUtils.validateBaseAccess(request.getBaseId());

        Base base = baseService.getBaseEntity(request.getBaseId());

        Asset asset;
        if (request.getAssetId() != null) {
            asset = assetService.getAssetEntity(request.getAssetId());
            if (!asset.getBase().getId().equals(base.getId())) {
                throw new InvalidOperationException("Asset with ID " + request.getAssetId() + " does not belong to base " + base.getName());
            }
        } else if (request.getAssetName() != null && request.getEquipmentTypeId() != null) {
            EquipmentType equipmentType = equipmentTypeService.getEquipmentTypeEntity(request.getEquipmentTypeId());
            asset = assetService.findOrCreateAsset(base, equipmentType, request.getAssetName().trim());
        } else {
            throw new InvalidOperationException("Either assetId or both assetName and equipmentTypeId must be provided");
        }

        // Increase inventory quantity at base
        int currentQuantity = asset.getQuantity() != null ? asset.getQuantity() : 0;
        asset.setQuantity(currentQuantity + request.getQuantity());
        assetRepository.save(asset);

        User currentUser = userRepository.findById(securityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        Purchase purchase = Purchase.builder()
                .base(base)
                .asset(asset)
                .quantity(request.getQuantity())
                .purchaseDate(request.getPurchaseDate() != null ? request.getPurchaseDate() : LocalDate.now())
                .supplier(request.getSupplier() != null ? request.getSupplier().trim() : null)
                .notes(request.getNotes() != null ? request.getNotes().trim() : null)
                .createdBy(currentUser)
                .build();

        Purchase saved = purchaseRepository.save(purchase);

        log.info("Purchase recorded: ID={}, Base={}, Asset={}, Qty={}", saved.getId(), base.getName(), asset.getName(), saved.getQuantity());

        auditLogService.logAction(
                currentUser.getUsername(),
                "PURCHASE_CREATED",
                "PURCHASE",
                saved.getId(),
                base.getId(),
                String.format("Purchased %d units of '%s' for base '%s'", saved.getQuantity(), asset.getName(), base.getName())
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with ID: " + id));

        securityUtils.validateBaseAccess(purchase.getBase().getId());

        return mapToResponse(purchase);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseResponse> getPurchases(
            Long baseId,
            Long equipmentTypeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Long resolvedBaseId = securityUtils.resolveAccessibleBaseId(baseId);

        return purchaseRepository.findFiltered(resolvedBaseId, equipmentTypeId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PurchaseResponse mapToResponse(Purchase purchase) {
        return PurchaseResponse.builder()
                .id(purchase.getId())
                .baseId(purchase.getBase().getId())
                .baseName(purchase.getBase().getName())
                .assetId(purchase.getAsset().getId())
                .assetName(purchase.getAsset().getName())
                .equipmentTypeId(purchase.getAsset().getEquipmentType().getId())
                .equipmentTypeName(purchase.getAsset().getEquipmentType().getName())
                .quantity(purchase.getQuantity())
                .purchaseDate(purchase.getPurchaseDate())
                .supplier(purchase.getSupplier())
                .notes(purchase.getNotes())
                .createdByUsername(purchase.getCreatedBy().getUsername())
                .createdAt(purchase.getCreatedAt())
                .build();
    }
}
