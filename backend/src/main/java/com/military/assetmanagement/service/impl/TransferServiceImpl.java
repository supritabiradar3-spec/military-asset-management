package com.military.assetmanagement.service.impl;

import com.military.assetmanagement.dto.TransferRequest;
import com.military.assetmanagement.dto.TransferResponse;
import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.Transfer;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.InsufficientInventoryException;
import com.military.assetmanagement.exception.InvalidOperationException;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.TransferRepository;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AssetService;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.BaseService;
import com.military.assetmanagement.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TransferServiceImpl implements TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);

    private final TransferRepository transferRepository;
    private final AssetRepository assetRepository;
    private final BaseService baseService;
    private final AssetService assetService;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final AuditLogService auditLogService;

    public TransferServiceImpl(
            TransferRepository transferRepository,
            AssetRepository assetRepository,
            BaseService baseService,
            AssetService assetService,
            UserRepository userRepository,
            SecurityUtils securityUtils,
            AuditLogService auditLogService
    ) {
        this.transferRepository = transferRepository;
        this.assetRepository = assetRepository;
        this.baseService = baseService;
        this.assetService = assetService;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public TransferResponse createTransfer(TransferRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new InvalidOperationException("Transfer quantity must be positive and greater than zero");
        }

        if (request.getFromBaseId() != null && Objects.equals(request.getFromBaseId(), request.getToBaseId())) {
            throw new InvalidOperationException("Source base and destination base cannot be the same");
        }

        // Validate source base access
        securityUtils.validateBaseAccess(request.getFromBaseId());

        Base fromBase = baseService.getBaseEntity(request.getFromBaseId());
        Base toBase = baseService.getBaseEntity(request.getToBaseId());
        Asset sourceAsset = assetService.getAssetEntity(request.getAssetId());

        if (!sourceAsset.getBase().getId().equals(fromBase.getId())) {
            throw new InvalidOperationException(
                    String.format("Asset '%s' does not belong to source base '%s'", sourceAsset.getName(), fromBase.getName())
            );
        }

        // Validate sufficient available inventory
        if (sourceAsset.getAvailableQuantity() < request.getQuantity()) {
            throw new InsufficientInventoryException(
                    String.format("Insufficient inventory for asset '%s' at base '%s'. Available: %d, Requested: %d",
                            sourceAsset.getName(), fromBase.getName(), sourceAsset.getAvailableQuantity(), request.getQuantity())
            );
        }

        // Decrease inventory at source base
        int currentSourceQty = sourceAsset.getQuantity() != null ? sourceAsset.getQuantity() : 0;
        sourceAsset.setQuantity(currentSourceQty - request.getQuantity());
        assetRepository.save(sourceAsset);

        // Increase inventory at destination base (find or create record for that equipment at destination)
        Asset destAsset = assetService.findOrCreateAsset(toBase, sourceAsset.getEquipmentType(), sourceAsset.getName());
        int currentDestQty = destAsset.getQuantity() != null ? destAsset.getQuantity() : 0;
        destAsset.setQuantity(currentDestQty + request.getQuantity());
        assetRepository.save(destAsset);

        User currentUser = userRepository.findById(securityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        Transfer transfer = Transfer.builder()
                .asset(sourceAsset)
                .fromBase(fromBase)
                .toBase(toBase)
                .quantity(request.getQuantity())
                .transferDate(request.getTransferDate() != null ? request.getTransferDate() : LocalDate.now())
                .reason(request.getReason() != null ? request.getReason().trim() : null)
                .createdBy(currentUser)
                .build();

        Transfer saved = transferRepository.save(transfer);

        log.info("Transfer recorded: ID={}, From={}, To={}, Asset={}, Qty={}",
                saved.getId(), fromBase.getName(), toBase.getName(), sourceAsset.getName(), saved.getQuantity());

        auditLogService.logAction(
                currentUser.getUsername(),
                "TRANSFER_CREATED",
                "TRANSFER",
                saved.getId(),
                fromBase.getId(),
                String.format("Transferred %d units of '%s' from base '%s' to base '%s'",
                        saved.getQuantity(), sourceAsset.getName(), fromBase.getName(), toBase.getName())
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferResponse getTransferById(Long id) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with ID: " + id));

        // Check if user has access to either fromBase or toBase
        if (!securityUtils.isAdmin()) {
            Long userBaseId = securityUtils.getCurrentUserBaseId();
            if (!transfer.getFromBase().getId().equals(userBaseId) && !transfer.getToBase().getId().equals(userBaseId)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Access denied: You are not authorized to view transfer ID " + id
                );
            }
        }

        return mapToResponse(transfer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponse> getTransfers(
            Long baseId,
            Long fromBaseId,
            Long toBaseId,
            Long equipmentTypeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Long resolvedBaseId = securityUtils.resolveAccessibleBaseId(baseId);

        return transferRepository.findFiltered(resolvedBaseId, fromBaseId, toBaseId, equipmentTypeId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransferResponse mapToResponse(Transfer transfer) {
        return TransferResponse.builder()
                .id(transfer.getId())
                .assetId(transfer.getAsset().getId())
                .assetName(transfer.getAsset().getName())
                .equipmentTypeId(transfer.getAsset().getEquipmentType().getId())
                .equipmentTypeName(transfer.getAsset().getEquipmentType().getName())
                .fromBaseId(transfer.getFromBase().getId())
                .fromBaseName(transfer.getFromBase().getName())
                .toBaseId(transfer.getToBase().getId())
                .toBaseName(transfer.getToBase().getName())
                .quantity(transfer.getQuantity())
                .transferDate(transfer.getTransferDate())
                .reason(transfer.getReason())
                .createdByUsername(transfer.getCreatedBy().getUsername())
                .createdAt(transfer.getCreatedAt())
                .build();
    }
}
