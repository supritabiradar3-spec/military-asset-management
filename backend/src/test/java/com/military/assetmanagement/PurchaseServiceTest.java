package com.military.assetmanagement;

import com.military.assetmanagement.dto.PurchaseRequest;
import com.military.assetmanagement.dto.PurchaseResponse;
import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;
import com.military.assetmanagement.entity.Purchase;
import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.RoleName;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.InvalidOperationException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.PurchaseRepository;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AssetService;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.BaseService;
import com.military.assetmanagement.service.EquipmentTypeService;
import com.military.assetmanagement.service.impl.PurchaseServiceImpl;
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
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private BaseService baseService;

    @Mock
    private EquipmentTypeService equipmentTypeService;

    @Mock
    private AssetService assetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private Base base;
    private EquipmentType equipmentType;
    private Asset asset;
    private User user;

    @BeforeEach
    void setUp() {
        base = Base.builder().id(1L).name("Forward Operating Base Alpha").location("Sector 1").build();
        equipmentType = EquipmentType.builder().id(1L).name("Rifles").description("Assault Rifles").build();
        asset = Asset.builder().id(10L).name("M4 Carbine").base(base).equipmentType(equipmentType).quantity(50).assignedQuantity(10).build();
        user = User.builder().id(100L).username("commander_alpha").role(new Role(RoleName.BASE_COMMANDER)).base(base).build();
    }

    @Test
    @DisplayName("Should successfully create purchase and increase asset inventory quantity")
    void testCreatePurchaseIncreasesInventory() {
        PurchaseRequest request = PurchaseRequest.builder()
                .baseId(1L)
                .assetId(10L)
                .quantity(25)
                .purchaseDate(LocalDate.now())
                .supplier("Defense Logistics Agency")
                .notes("Standard resupply")
                .build();

        when(baseService.getBaseEntity(1L)).thenReturn(base);
        when(assetService.getAssetEntity(10L)).thenReturn(asset);
        when(securityUtils.getCurrentUserId()).thenReturn(100L);
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> {
            Purchase p = invocation.getArgument(0);
            p.setId(500L);
            return p;
        });

        PurchaseResponse response = purchaseService.createPurchase(request);

        assertNotNull(response);
        assertEquals(500L, response.getId());
        assertEquals(25, response.getQuantity());
        assertEquals(75, asset.getQuantity()); // 50 + 25 = 75

        verify(assetRepository, times(1)).save(asset);
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
        verify(auditLogService, times(1)).logAction(eq("commander_alpha"), eq("PURCHASE_CREATED"), eq("PURCHASE"), eq(500L), eq(1L), anyString());
    }

    @Test
    @DisplayName("Should reject purchase with non-positive quantity")
    void testCreatePurchaseNonPositiveQuantity() {
        PurchaseRequest zeroRequest = PurchaseRequest.builder()
                .baseId(1L)
                .assetId(10L)
                .quantity(0)
                .build();

        assertThrows(InvalidOperationException.class, () -> purchaseService.createPurchase(zeroRequest));

        PurchaseRequest negativeRequest = PurchaseRequest.builder()
                .baseId(1L)
                .assetId(10L)
                .quantity(-5)
                .build();

        assertThrows(InvalidOperationException.class, () -> purchaseService.createPurchase(negativeRequest));

        verify(purchaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject purchase when user has no access to target base")
    void testCreatePurchaseUnauthorizedBase() {
        PurchaseRequest request = PurchaseRequest.builder()
                .baseId(2L)
                .assetId(10L)
                .quantity(10)
                .build();

        doThrow(new AccessDeniedException("Access denied: You are not authorized for base ID 2"))
                .when(securityUtils).validateBaseAccess(2L);

        assertThrows(AccessDeniedException.class, () -> purchaseService.createPurchase(request));

        verify(purchaseRepository, never()).save(any());
    }
}
