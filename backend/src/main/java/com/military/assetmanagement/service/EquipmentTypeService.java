package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.EquipmentTypeRequest;
import com.military.assetmanagement.dto.EquipmentTypeResponse;
import com.military.assetmanagement.entity.EquipmentType;

import java.util.List;

public interface EquipmentTypeService {
    List<EquipmentTypeResponse> getAllEquipmentTypes();
    EquipmentTypeResponse getEquipmentTypeById(Long id);
    EquipmentType getEquipmentTypeEntity(Long id);
    EquipmentTypeResponse createEquipmentType(EquipmentTypeRequest request);
}
