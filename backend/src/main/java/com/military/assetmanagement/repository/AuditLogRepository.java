package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:baseId IS NULL OR a.baseId = :baseId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:entityType IS NULL OR a.entityType = :entityType) AND " +
           "(:startDateTime IS NULL OR a.timestamp >= :startDateTime) AND " +
           "(:endDateTime IS NULL OR a.timestamp <= :endDateTime) " +
           "ORDER BY a.timestamp DESC")
    List<AuditLog> findFiltered(
            @Param("baseId") Long baseId,
            @Param("action") String action,
            @Param("entityType") String entityType,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
