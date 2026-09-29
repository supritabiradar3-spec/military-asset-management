package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.EquipmentTypeRequest;
import com.military.assetmanagement.dto.EquipmentTypeResponse;
import com.military.assetmanagement.service.EquipmentTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-types")
public class EquipmentTypeController {

    private final EquipmentTypeService equipmentTypeService;

    public EquipmentTypeController(EquipmentTypeService equipmentTypeService) {
        this.equipmentTypeService = equipmentTypeService;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentTypeResponse>> getAllEquipmentTypes() {
        List<EquipmentTypeResponse> types = equipmentTypeService.getAllEquipmentTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentTypeResponse> getEquipmentTypeById(@PathVariable Long id) {
        EquipmentTypeResponse type = equipmentTypeService.getEquipmentTypeById(id);
        return ResponseEntity.ok(type);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentTypeResponse> createEquipmentType(@Valid @RequestBody EquipmentTypeRequest request) {
        EquipmentTypeResponse created = equipmentTypeService.createEquipmentType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
