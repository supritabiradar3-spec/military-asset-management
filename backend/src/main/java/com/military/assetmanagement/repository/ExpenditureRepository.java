package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findByBaseId(Long baseId);

    @Query("SELECT e FROM Expenditure e WHERE " +
           "(:baseId IS NULL OR e.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR e.asset.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR e.expenditureDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenditureDate <= :endDate) " +
           "ORDER BY e.expenditureDate DESC, e.createdAt DESC")
    List<Expenditure> findFiltered(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(e.quantity), 0) FROM Expenditure e WHERE " +
           "(:baseId IS NULL OR e.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR e.asset.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR e.expenditureDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenditureDate <= :endDate)")
    long sumQuantity(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(e.quantity), 0) FROM Expenditure e WHERE " +
           "(:baseId IS NULL OR e.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR e.asset.equipmentType.id = :equipmentTypeId) AND " +
           "e.expenditureDate < :beforeDate")
    long sumQuantityBeforeDate(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("beforeDate") LocalDate beforeDate
    );
}
