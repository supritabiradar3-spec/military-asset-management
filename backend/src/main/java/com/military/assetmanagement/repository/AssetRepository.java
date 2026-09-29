package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByBase(Base base);
    List<Asset> findByBaseId(Long baseId);
    List<Asset> findByEquipmentType(EquipmentType equipmentType);
    List<Asset> findByEquipmentTypeId(Long equipmentTypeId);
    List<Asset> findByBaseIdAndEquipmentTypeId(Long baseId, Long equipmentTypeId);
    Optional<Asset> findByNameAndBaseId(String name, Long baseId);
    boolean existsByNameAndBaseId(String name, Long baseId);
}
