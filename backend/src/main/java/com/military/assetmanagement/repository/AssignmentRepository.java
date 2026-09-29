package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByBaseId(Long baseId);

    @Query("SELECT a FROM Assignment a WHERE " +
           "(:baseId IS NULL OR a.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR a.asset.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR a.assignmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.assignmentDate <= :endDate) " +
           "ORDER BY a.assignmentDate DESC, a.createdAt DESC")
    List<Assignment> findFiltered(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(a.quantity), 0) FROM Assignment a WHERE " +
           "(:baseId IS NULL OR a.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR a.asset.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR a.assignmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.assignmentDate <= :endDate) AND " +
           "a.status = 'ACTIVE'")
    long sumActiveQuantity(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(a.quantity), 0) FROM Assignment a WHERE " +
           "(:baseId IS NULL OR a.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR a.asset.equipmentType.id = :equipmentTypeId) AND " +
           "a.status = 'ACTIVE'")
    long sumAllActiveQuantity(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId
    );
}
