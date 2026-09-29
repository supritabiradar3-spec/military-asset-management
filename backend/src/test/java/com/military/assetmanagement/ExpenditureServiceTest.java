package com.military.assetmanagement;

import com.military.assetmanagement.dto.ExpenditureRequest;
import com.military.assetmanagement.dto.ExpenditureResponse;
import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;
import com.military.assetmanagement.entity.Expenditure;
import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.RoleName;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.InsufficientInventoryException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.ExpenditureRepository;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AssetService;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.BaseService;
import com.military.assetmanagement.service.impl.ExpenditureServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenditureServiceTest {

    @Mock
    private ExpenditureRepository expenditureRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private BaseService baseService;

    @Mock
    private AssetService assetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ExpenditureServiceImpl expenditureService;

    private Base base;
    private EquipmentType equipmentType;
    private Asset asset;
    private User user;

    @BeforeEach
    void setUp() {
        base = Base.builder().id(1L).name("Base Alpha").location("Sector 1").build();
        equipmentType = EquipmentType.builder().id(1L).name("Ammunition").description("5.56mm Ammo").build();
        // 1000 total, 200 assigned -> 800 available
        asset = Asset.builder().id(10L).name("5.56mm Cartridges").base(base).equipmentType(equipmentType).quantity(1000).assignedQuantity(200).build();
        user = User.builder().id(100L).username("commander_alpha").role(new Role(RoleName.BASE_COMMANDER)).base(base).build();
    }

    @Test
    @DisplayName("Should expend asset and permanently reduce total quantity")
    void testCreateExpenditureSuccess() {
        ExpenditureRequest request = ExpenditureRequest.builder()
                .assetId(10L)
                .baseId(1L)
                .quantity(300)
                .reason("Live Fire Exercise")
                .expenditureDate(LocalDate.now())
                .build();

        when(securityUtils.isLogisticsOfficer()).thenReturn(false);
        when(baseService.getBaseEntity(1L)).thenReturn(base);
        when(assetService.getAssetEntity(10L)).thenReturn(asset);
        when(securityUtils.getCurrentUserId()).thenReturn(100L);
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(expenditureRepository.save(any(Expenditure.class))).thenAnswer(invocation -> {
            Expenditure e = invocation.getArgument(0);
            e.setId(900L);
            return e;
        });

        ExpenditureResponse response = expenditureService.createExpenditure(request);

        assertNotNull(response);
        assertEquals(900L, response.getId());
        assertEquals(300, response.getQuantity());

        // Total permanently reduced: 1000 - 300 = 700
        assertEquals(700, asset.getQuantity());

        verify(assetRepository, times(1)).save(asset);
        verify(expenditureRepository, times(1)).save(any(Expenditure.class));
        verify(auditLogService, times(1)).logAction(eq("commander_alpha"), eq("EXPENDITURE_CREATED"), eq("EXPENDITURE"), eq(900L), eq(1L), anyString());
    }

    @Test
    @DisplayName("Should reject expenditure if requested quantity exceeds available stock")
    void testCreateExpenditureInsufficientAvailability() {
        // Requested 850, available is 800
        ExpenditureRequest request = ExpenditureRequest.builder()
                .assetId(10L)
                .baseId(1L)
                .quantity(850)
                .reason("Combat operation")
                .build();

        when(securityUtils.isLogisticsOfficer()).thenReturn(false);
        when(baseService.getBaseEntity(1L)).thenReturn(base);
        when(assetService.getAssetEntity(10L)).thenReturn(asset);

        assertThrows(InsufficientInventoryException.class, () -> expenditureService.createExpenditure(request));
        verify(expenditureRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject expenditure if user is LOGISTICS_OFFICER")
    void testCreateExpenditureLogisticsOfficerDenied() {
        ExpenditureRequest request = ExpenditureRequest.builder()
                .assetId(10L)
                .baseId(1L)
                .quantity(50)
                .reason("Training")
                .build();

        when(securityUtils.isLogisticsOfficer()).thenReturn(true);

        assertThrows(AccessDeniedException.class, () -> expenditureService.createExpenditure(request));
        verify(expenditureRepository, never()).save(any());
    }
}
