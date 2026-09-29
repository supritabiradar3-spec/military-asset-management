package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("SELECT t FROM Transfer t WHERE " +
           "(:baseId IS NULL OR t.fromBase.id = :baseId OR t.toBase.id = :baseId) AND " +
           "(:fromBaseId IS NULL OR t.fromBase.id = :fromBaseId) AND " +
           "(:toBaseId IS NULL OR t.toBase.id = :toBaseId) AND " +
           "(:equipmentTypeId IS NULL OR t.asset.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate) " +
           "ORDER BY t.transferDate DESC, t.createdAt DESC")
    List<Transfer> findFiltered(
            @Param("baseId") Long baseId,
            @Param("fromBaseId") Long fromBaseId,
            @Param("toBaseId") Long toBaseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "(:toBaseId IS NULL OR t.toBase.id = :toBaseId) AND " +
           "(:equipmentTypeId IS NULL OR t.asset.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate)")
    long sumTransferIn(
            @Param("toBaseId") Long toBaseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "(:fromBaseId IS NULL OR t.fromBase.id = :fromBaseId) AND " +
           "(:equipmentTypeId IS NULL OR t.asset.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate)")
    long sumTransferOut(
            @Param("fromBaseId") Long fromBaseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "(:toBaseId IS NULL OR t.toBase.id = :toBaseId) AND " +
           "(:equipmentTypeId IS NULL OR t.asset.equipmentType.id = :equipmentTypeId) AND " +
           "t.transferDate < :beforeDate")
    long sumTransferInBeforeDate(
            @Param("toBaseId") Long toBaseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("beforeDate") LocalDate beforeDate
    );

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "(:fromBaseId IS NULL OR t.fromBase.id = :fromBaseId) AND " +
           "(:equipmentTypeId IS NULL OR t.asset.equipmentType.id = :equipmentTypeId) AND " +
           "t.transferDate < :beforeDate")
    long sumTransferOutBeforeDate(
            @Param("fromBaseId") Long fromBaseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("beforeDate") LocalDate beforeDate
    );
}
