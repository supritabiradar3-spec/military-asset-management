package com.military.assetmanagement;

import com.military.assetmanagement.dto.TransferRequest;
import com.military.assetmanagement.dto.TransferResponse;
import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;
import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.RoleName;
import com.military.assetmanagement.entity.Transfer;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.InsufficientInventoryException;
import com.military.assetmanagement.exception.InvalidOperationException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.TransferRepository;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AssetService;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.BaseService;
import com.military.assetmanagement.service.impl.TransferServiceImpl;
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
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

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
    private TransferServiceImpl transferService;

    private Base fromBase;
    private Base toBase;
    private EquipmentType equipmentType;
    private Asset sourceAsset;
    private Asset destAsset;
    private User user;

    @BeforeEach
    void setUp() {
        fromBase = Base.builder().id(1L).name("Base Alpha").location("Sector 1").build();
        toBase = Base.builder().id(2L).name("Base Bravo").location("Sector 2").build();
        equipmentType = EquipmentType.builder().id(1L).name("Radios").description("Communication").build();

        // Source asset has 50 total, 10 assigned -> 40 available
        sourceAsset = Asset.builder().id(10L).name("Tactical Radio").base(fromBase).equipmentType(equipmentType).quantity(50).assignedQuantity(10).build();
        destAsset = Asset.builder().id(20L).name("Tactical Radio").base(toBase).equipmentType(equipmentType).quantity(10).assignedQuantity(0).build();

        user = User.builder().id(100L).username("admin").role(new Role(RoleName.ADMIN)).build();
    }

    @Test
    @DisplayName("Should successfully transfer inventory from source base to destination base")
    void testCreateTransferSuccess() {
        TransferRequest request = TransferRequest.builder()
                .assetId(10L)
                .fromBaseId(1L)
                .toBaseId(2L)
                .quantity(15)
                .transferDate(LocalDate.now())
                .reason("Operation Support")
                .build();

        when(baseService.getBaseEntity(1L)).thenReturn(fromBase);
        when(baseService.getBaseEntity(2L)).thenReturn(toBase);
        when(assetService.getAssetEntity(10L)).thenReturn(sourceAsset);
        when(assetService.findOrCreateAsset(toBase, equipmentType, "Tactical Radio")).thenReturn(destAsset);
        when(securityUtils.getCurrentUserId()).thenReturn(100L);
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer t = invocation.getArgument(0);
            t.setId(700L);
            return t;
        });

        TransferResponse response = transferService.createTransfer(request);

        assertNotNull(response);
        assertEquals(700L, response.getId());
        assertEquals(15, response.getQuantity());

        // Source decreased: 50 - 15 = 35
        assertEquals(35, sourceAsset.getQuantity());
        // Dest increased: 10 + 15 = 25
        assertEquals(25, destAsset.getQuantity());

        verify(assetRepository, times(1)).save(sourceAsset);
        verify(assetRepository, times(1)).save(destAsset);
        verify(transferRepository, times(1)).save(any(Transfer.class));
        verify(auditLogService, times(1)).logAction(eq("admin"), eq("TRANSFER_CREATED"), eq("TRANSFER"), eq(700L), eq(1L), anyString());
    }

    @Test
    @DisplayName("Should reject transfer when source and destination bases are identical")
    void testCreateTransferSameBaseRejected() {
        TransferRequest request = TransferRequest.builder()
                .assetId(10L)
                .fromBaseId(1L)
                .toBaseId(1L)
                .quantity(5)
                .build();

        assertThrows(InvalidOperationException.class, () -> transferService.createTransfer(request));
        verify(transferRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject transfer when source base has insufficient available inventory")
    void testCreateTransferInsufficientInventory() {
        // Requested 45, but available is only 40 (50 total - 10 assigned)
        TransferRequest request = TransferRequest.builder()
                .assetId(10L)
                .fromBaseId(1L)
                .toBaseId(2L)
                .quantity(45)
                .build();

        when(baseService.getBaseEntity(1L)).thenReturn(fromBase);
        when(baseService.getBaseEntity(2L)).thenReturn(toBase);
        when(assetService.getAssetEntity(10L)).thenReturn(sourceAsset);

        assertThrows(InsufficientInventoryException.class, () -> transferService.createTransfer(request));
        verify(transferRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject transfer when user is unauthorized for source base")
    void testCreateTransferUnauthorizedSourceBase() {
        TransferRequest request = TransferRequest.builder()
                .assetId(10L)
                .fromBaseId(1L)
                .toBaseId(2L)
                .quantity(5)
                .build();

        doThrow(new AccessDeniedException("Access denied")).when(securityUtils).validateBaseAccess(1L);

        assertThrows(AccessDeniedException.class, () -> transferService.createTransfer(request));
        verify(transferRepository, never()).save(any());
    }
}
