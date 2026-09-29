package com.military.assetmanagement;

import com.military.assetmanagement.dto.AssignmentRequest;
import com.military.assetmanagement.dto.AssignmentResponse;
import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Assignment;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;
import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.RoleName;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.InsufficientInventoryException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.AssignmentRepository;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.security.SecurityUtils;
import com.military.assetmanagement.service.AssetService;
import com.military.assetmanagement.service.AuditLogService;
import com.military.assetmanagement.service.BaseService;
import com.military.assetmanagement.service.impl.AssignmentServiceImpl;
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
class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

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
    private AssignmentServiceImpl assignmentService;

    private Base base;
    private EquipmentType equipmentType;
    private Asset asset;
    private User user;

    @BeforeEach
    void setUp() {
        base = Base.builder().id(1L).name("Base Alpha").location("Sector 1").build();
        equipmentType = EquipmentType.builder().id(1L).name("Night Vision").description("Optics").build();
        // 20 total, 5 assigned -> 15 available
        asset = Asset.builder().id(10L).name("NVG Goggles").base(base).equipmentType(equipmentType).quantity(20).assignedQuantity(5).build();
        user = User.builder().id(100L).username("commander_alpha").role(new Role(RoleName.BASE_COMMANDER)).base(base).build();
    }

    @Test
    @DisplayName("Should assign equipment to personnel and increase assigned quantity")
    void testCreateAssignmentSuccess() {
        AssignmentRequest request = AssignmentRequest.builder()
                .assetId(10L)
                .baseId(1L)
                .personnelName("Sgt. John Doe")
                .personnelIdentifier("SN-98231")
                .quantity(3)
                .assignmentDate(LocalDate.now())
                .notes("Night Recon Squad")
                .build();

        when(securityUtils.isLogisticsOfficer()).thenReturn(false);
        when(baseService.getBaseEntity(1L)).thenReturn(base);
        when(assetService.getAssetEntity(10L)).thenReturn(asset);
        when(securityUtils.getCurrentUserId()).thenReturn(100L);
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> {
            Assignment a = invocation.getArgument(0);
            a.setId(800L);
            return a;
        });

        AssignmentResponse response = assignmentService.createAssignment(request);

        assertNotNull(response);
        assertEquals(800L, response.getId());
        assertEquals("Sgt. John Doe", response.getPersonnelName());
        assertEquals(3, response.getQuantity());

        // Total remains 20, assigned increased to 8 (5 + 3), available is now 12
        assertEquals(20, asset.getQuantity());
        assertEquals(8, asset.getAssignedQuantity());
        assertEquals(12, asset.getAvailableQuantity());

        verify(assetRepository, times(1)).save(asset);
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
        verify(auditLogService, times(1)).logAction(eq("commander_alpha"), eq("ASSIGNMENT_CREATED"), eq("ASSIGNMENT"), eq(800L), eq(1L), anyString());
    }

    @Test
    @DisplayName("Should reject assignment if requested quantity exceeds available unassigned stock")
    void testCreateAssignmentInsufficientAvailability() {
        // Requested 16, available is 15
        AssignmentRequest request = AssignmentRequest.builder()
                .assetId(10L)
                .baseId(1L)
                .personnelName("Capt. Miller")
                .quantity(16)
                .build();

        when(securityUtils.isLogisticsOfficer()).thenReturn(false);
        when(baseService.getBaseEntity(1L)).thenReturn(base);
        when(assetService.getAssetEntity(10L)).thenReturn(asset);

        assertThrows(InsufficientInventoryException.class, () -> assignmentService.createAssignment(request));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject assignment if user is LOGISTICS_OFFICER")
    void testCreateAssignmentLogisticsOfficerDenied() {
        AssignmentRequest request = AssignmentRequest.builder()
                .assetId(10L)
                .baseId(1L)
                .personnelName("Sgt. Miller")
                .quantity(1)
                .build();

        when(securityUtils.isLogisticsOfficer()).thenReturn(true);

        assertThrows(AccessDeniedException.class, () -> assignmentService.createAssignment(request));
        verify(assignmentRepository, never()).save(any());
    }
}
