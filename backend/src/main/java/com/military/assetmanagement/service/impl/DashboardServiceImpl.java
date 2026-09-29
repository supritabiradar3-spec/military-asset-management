package com.military.assetmanagement.service.impl;

import com.military.assetmanagement.dto.DashboardMetricsResponse;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;
import com.military.assetmanagement.repository.AssignmentRepository;
import com.military.assetmanagement.repository.ExpenditureRepository;
import com.military.assetmanagement.repository.PurchaseRepository;
import com.military.assetmanagement.repository.TransferRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.BaseService;
import com.military.assetmanagement.service.DashboardService;
import com.military.assetmanagement.service.EquipmentTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final BaseService baseService;
    private final EquipmentTypeService equipmentTypeService;
    private final SecurityUtils securityUtils;

    public DashboardServiceImpl(
            PurchaseRepository purchaseRepository,
            TransferRepository transferRepository,
            AssignmentRepository assignmentRepository,
            ExpenditureRepository expenditureRepository,
            BaseService baseService,
            EquipmentTypeService equipmentTypeService,
            SecurityUtils securityUtils
    ) {
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
        this.baseService = baseService;
        this.equipmentTypeService = equipmentTypeService;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardMetricsResponse getDashboardMetrics(
            Long baseId,
            Long equipmentTypeId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Long resolvedBaseId = securityUtils.resolveAccessibleBaseId(baseId);

        // 1. Calculate Period Movements
        long purchases = purchaseRepository.sumQuantity(resolvedBaseId, equipmentTypeId, startDate, endDate);
        long transferIn = transferRepository.sumTransferIn(resolvedBaseId, equipmentTypeId, startDate, endDate);
        long transferOut = transferRepository.sumTransferOut(resolvedBaseId, equipmentTypeId, startDate, endDate);
        long netMovement = purchases + transferIn - transferOut;
        long expended = expenditureRepository.sumQuantity(resolvedBaseId, equipmentTypeId, startDate, endDate);

        long assigned;
        if (startDate != null || endDate != null) {
            assigned = assignmentRepository.sumActiveQuantity(resolvedBaseId, equipmentTypeId, startDate, endDate);
        } else {
            assigned = assignmentRepository.sumAllActiveQuantity(resolvedBaseId, equipmentTypeId);
        }

        // 2. Calculate Opening Balance (Prior Transactions before startDate)
        long openingBalance = 0;
        if (startDate != null) {
            long priorPurchases = purchaseRepository.sumQuantityBeforeDate(resolvedBaseId, equipmentTypeId, startDate);
            long priorTransferIn = transferRepository.sumTransferInBeforeDate(resolvedBaseId, equipmentTypeId, startDate);
            long priorTransferOut = transferRepository.sumTransferOutBeforeDate(resolvedBaseId, equipmentTypeId, startDate);
            long priorExpended = expenditureRepository.sumQuantityBeforeDate(resolvedBaseId, equipmentTypeId, startDate);

            openingBalance = Math.max(0, priorPurchases + priorTransferIn - priorTransferOut - priorExpended);
        }

        // 3. Calculate Closing Balance
        long closingBalance = Math.max(0, openingBalance + netMovement - expended);

        // 4. Retrieve metadata
        String baseName = null;
        if (resolvedBaseId != null) {
            try {
                Base base = baseService.getBaseEntity(resolvedBaseId);
                baseName = base.getName();
            } catch (Exception ignored) {
            }
        }

        String equipmentTypeName = null;
        if (equipmentTypeId != null) {
            try {
                EquipmentType eqType = equipmentTypeService.getEquipmentTypeEntity(equipmentTypeId);
                equipmentTypeName = eqType.getName();
            } catch (Exception ignored) {
            }
        }

        return DashboardMetricsResponse.builder()
                .openingBalance(openingBalance)
                .purchases(purchases)
                .transferIn(transferIn)
                .transferOut(transferOut)
                .netMovement(netMovement)
                .assigned(assigned)
                .expended(expended)
                .closingBalance(closingBalance)
                .startDate(startDate)
                .endDate(endDate)
                .baseId(resolvedBaseId)
                .baseName(baseName)
                .equipmentTypeId(equipmentTypeId)
                .equipmentTypeName(equipmentTypeName)
                .build();
    }
}
