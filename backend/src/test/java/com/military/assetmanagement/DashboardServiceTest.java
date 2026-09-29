package com.military.assetmanagement;

import com.military.assetmanagement.dto.DashboardMetricsResponse;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;
import com.military.assetmanagement.repository.AssignmentRepository;
import com.military.assetmanagement.repository.ExpenditureRepository;
import com.military.assetmanagement.repository.PurchaseRepository;
import com.military.assetmanagement.repository.TransferRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.BaseService;
import com.military.assetmanagement.service.EquipmentTypeService;
import com.military.assetmanagement.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private ExpenditureRepository expenditureRepository;

    @Mock
    private BaseService baseService;

    @Mock
    private EquipmentTypeService equipmentTypeService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private Base base;
    private EquipmentType equipmentType;

    @BeforeEach
    void setUp() {
        base = Base.builder().id(1L).name("Forward Operating Base Alpha").location("Sector 1").build();
        equipmentType = EquipmentType.builder().id(2L).name("Vehicles").description("Armored Vehicles").build();
    }

    @Test
    @DisplayName("Should correctly calculate date-based opening balance, net movement and closing balance")
    void testGetDashboardMetricsWithDateRange() {
        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 9, 30);

        when(securityUtils.resolveAccessibleBaseId(1L)).thenReturn(1L);

        // Period movements
        when(purchaseRepository.sumQuantity(1L, 2L, startDate, endDate)).thenReturn(100L);
        when(transferRepository.sumTransferIn(1L, 2L, startDate, endDate)).thenReturn(30L);
        when(transferRepository.sumTransferOut(1L, 2L, startDate, endDate)).thenReturn(20L);
        when(expenditureRepository.sumQuantity(1L, 2L, startDate, endDate)).thenReturn(15L);
        when(assignmentRepository.sumActiveQuantity(1L, 2L, startDate, endDate)).thenReturn(40L);

        // Prior movements before startDate
        when(purchaseRepository.sumQuantityBeforeDate(1L, 2L, startDate)).thenReturn(200L);
        when(transferRepository.sumTransferInBeforeDate(1L, 2L, startDate)).thenReturn(50L);
        when(transferRepository.sumTransferOutBeforeDate(1L, 2L, startDate)).thenReturn(40L);
        when(expenditureRepository.sumQuantityBeforeDate(1L, 2L, startDate)).thenReturn(10L);

        when(baseService.getBaseEntity(1L)).thenReturn(base);
        when(equipmentTypeService.getEquipmentTypeEntity(2L)).thenReturn(equipmentType);

        DashboardMetricsResponse metrics = dashboardService.getDashboardMetrics(1L, 2L, startDate, endDate);

        assertNotNull(metrics);

        // Prior: 200 + 50 - 40 - 10 = 200 Opening Balance
        assertEquals(200L, metrics.getOpeningBalance());

        // Purchases: 100
        assertEquals(100L, metrics.getPurchases());
        assertEquals(30L, metrics.getTransferIn());
        assertEquals(20L, metrics.getTransferOut());

        // Net Movement: Purchases (100) + Transfer In (30) - Transfer Out (20) = 110
        assertEquals(110L, metrics.getNetMovement());

        // Expended: 15
        assertEquals(15L, metrics.getExpended());

        // Assigned: 40
        assertEquals(40L, metrics.getAssigned());

        // Closing Balance: Opening (200) + Net Movement (110) - Expended (15) = 295
        assertEquals(295L, metrics.getClosingBalance());

        assertEquals("Forward Operating Base Alpha", metrics.getBaseName());
        assertEquals("Vehicles", metrics.getEquipmentTypeName());
    }

    @Test
    @DisplayName("Should correctly calculate lifetime dashboard metrics when no date filter is provided")
    void testGetDashboardMetricsAllTime() {
        when(securityUtils.resolveAccessibleBaseId(null)).thenReturn(null);

        when(purchaseRepository.sumQuantity(null, null, null, null)).thenReturn(500L);
        when(transferRepository.sumTransferIn(null, null, null, null)).thenReturn(100L);
        when(transferRepository.sumTransferOut(null, null, null, null)).thenReturn(100L);
        when(expenditureRepository.sumQuantity(null, null, null, null)).thenReturn(50L);
        when(assignmentRepository.sumAllActiveQuantity(null, null)).thenReturn(150L);

        DashboardMetricsResponse metrics = dashboardService.getDashboardMetrics(null, null, null, null);

        assertNotNull(metrics);
        assertEquals(0L, metrics.getOpeningBalance());
        assertEquals(500L, metrics.getPurchases());
        assertEquals(100L, metrics.getTransferIn());
        assertEquals(100L, metrics.getTransferOut());
        assertEquals(500L, metrics.getNetMovement());
        assertEquals(50L, metrics.getExpended());
        assertEquals(150L, metrics.getAssigned());
        // Closing Balance: 0 + 500 - 50 = 450
        assertEquals(450L, metrics.getClosingBalance());
    }
}
