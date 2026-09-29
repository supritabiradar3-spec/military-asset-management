package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByBaseId(Long baseId);

    @Query("SELECT p FROM Purchase p WHERE " +
           "(:baseId IS NULL OR p.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR p.asset.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR p.purchaseDate >= :startDate) AND " +
           "(:endDate IS NULL OR p.purchaseDate <= :endDate) " +
           "ORDER BY p.purchaseDate DESC, p.createdAt DESC")
    List<Purchase> findFiltered(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Purchase p WHERE " +
           "(:baseId IS NULL OR p.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR p.asset.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR p.purchaseDate >= :startDate) AND " +
           "(:endDate IS NULL OR p.purchaseDate <= :endDate)")
    long sumQuantity(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Purchase p WHERE " +
           "(:baseId IS NULL OR p.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR p.asset.equipmentType.id = :equipmentTypeId) AND " +
           "p.purchaseDate < :beforeDate")
    long sumQuantityBeforeDate(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("beforeDate") LocalDate beforeDate
    );
}
