package com.military.assetmanagement.service.impl;

import com.military.assetmanagement.dto.ExpenditureRequest;
import com.military.assetmanagement.dto.ExpenditureResponse;
import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.Expenditure;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.InsufficientInventoryException;
import com.military.assetmanagement.exception.InvalidOperationException;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.ExpenditureRepository;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AssetService;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.BaseService;
import com.military.assetmanagement.service.ExpenditureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenditureServiceImpl implements ExpenditureService {

    private static final Logger log = LoggerFactory.getLogger(ExpenditureServiceImpl.class);

    private final ExpenditureRepository expenditureRepository;
    private final AssetRepository assetRepository;
    private final BaseService baseService;
    private final AssetService assetService;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;

    public ExpenditureServiceImpl(
            ExpenditureRepository expenditureRepository,
            AssetRepository assetRepository,
            BaseService baseService,
            AssetService assetService,
            UserRepository userRepository,
            SecurityUtils securityUtils,
            AuditLogService auditLogService
    ) {
        this.expenditureRepository = expenditureRepository;
        this.assetRepository = assetRepository;
        this.baseService = baseService;
        this.assetService = assetService;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public ExpenditureResponse createExpenditure(ExpenditureRequest request) {
        if (securityUtils.isLogisticsOfficer()) {
            throw new AccessDeniedException("LOGISTICS_OFFICER is not authorized to create expenditures");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new InvalidOperationException("Expenditure quantity must be positive and greater than zero");
        }

        securityUtils.validateBaseAccess(request.getBaseId());

        Base base = baseService.getBaseEntity(request.getBaseId());
        Asset asset = assetService.getAssetEntity(request.getAssetId());

        if (!asset.getBase().getId().equals(base.getId())) {
            throw new InvalidOperationException(
                    String.format("Asset '%s' does not belong to base '%s'", asset.getName(), base.getName())
            );
        }

        // Validate available inventory
        if (asset.getAvailableQuantity() < request.getQuantity()) {
            throw new InsufficientInventoryException(
                    String.format("Insufficient inventory for asset '%s' at base '%s'. Available: %d, Requested: %d",
                            asset.getName(), base.getName(), asset.getAvailableQuantity(), request.getQuantity())
            );
        }

        // Permanently reduce total quantity in base stock
        int currentQuantity = asset.getQuantity() != null ? asset.getQuantity() : 0;
        asset.setQuantity(currentQuantity - request.getQuantity());
        assetRepository.save(asset);

        User currentUser = userRepository.findById(securityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        Expenditure expenditure = Expenditure.builder()
                .asset(asset)
                .base(base)
                .quantity(request.getQuantity())
                .reason(request.getReason().trim())
                .expenditureDate(request.getExpenditureDate() != null ? request.getExpenditureDate() : LocalDate.now())
                .createdBy(currentUser)
                .build();

        Expenditure saved = expenditureRepository.save(expenditure);

        log.info("Expenditure recorded: ID={}, Base={}, Asset={}, Qty={}, Reason={}",
                saved.getId(), base.getName(), asset.getName(), saved.getQuantity(), saved.getReason());

        auditLogService.logAction(
                currentUser.getUsername(),
                "EXPENDITURE_CREATED",
                "EXPENDITURE",
                saved.getId(),
                base.getId(),
                String.format("Expended %d units of '%s' at base '%s'. Reason: %s",
                        saved.getQuantity(), asset.getName(), base.getName(), saved.getReason())
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenditureResponse getExpenditureById(Long id) {
        Expenditure expenditure = expenditureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expenditure not found with ID: " + id));

        securityUtils.validateBaseAccess(expenditure.getBase().getId());

        return mapToResponse(expenditure);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenditureResponse> getExpenditures(
            Long baseId,
            Long equipmentTypeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Long resolvedBaseId = securityUtils.resolveAccessibleBaseId(baseId);

        return expenditureRepository.findFiltered(resolvedBaseId, equipmentTypeId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExpenditureResponse mapToResponse(Expenditure expenditure) {
        return ExpenditureResponse.builder()
                .id(expenditure.getId())
                .assetId(expenditure.getAsset().getId())
                .assetName(expenditure.getAsset().getName())
                .equipmentTypeId(expenditure.getAsset().getEquipmentType().getId())
                .equipmentTypeName(expenditure.getAsset().getEquipmentType().getName())
                .baseId(expenditure.getBase().getId())
                .baseName(expenditure.getBase().getName())
                .quantity(expenditure.getQuantity())
                .reason(expenditure.getReason())
                .expenditureDate(expenditure.getExpenditureDate())
                .createdByUsername(expenditure.getCreatedBy().getUsername())
                .createdAt(expenditure.getCreatedAt())
                .build();
    }
}
