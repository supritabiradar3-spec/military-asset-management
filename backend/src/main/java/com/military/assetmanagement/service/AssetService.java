package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.AssetRequest;
import com.military.assetmanagement.dto.AssetResponse;
import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;

import java.util.List;

public interface AssetService {
    List<AssetResponse> getAssets(Long baseId, Long equipmentTypeId);
    AssetResponse getAssetById(Long id);
    Asset getAssetEntity(Long id);
    AssetResponse createAsset(AssetRequest request);
    Asset findOrCreateAsset(Base base, EquipmentType equipmentType, String name);
}
