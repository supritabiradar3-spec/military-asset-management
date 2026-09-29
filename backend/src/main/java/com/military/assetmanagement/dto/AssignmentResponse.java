package com.military.assetmanagement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssignmentResponse {

    private Long id;
    private Long assetId;
    private String assetName;
    private Long equipmentTypeId;
    private String equipmentTypeName;
    private Long baseId;
    private String baseName;
    private String personnelName;
    private String personnelIdentifier;
    private Integer quantity;
    private LocalDate assignmentDate;
    private String status;
    private String notes;
    private String createdByUsername;
    private LocalDateTime createdAt;

    public AssignmentResponse() {
    }

    public AssignmentResponse(
            Long id,
            Long assetId,
            String assetName,
            Long equipmentTypeId,
            String equipmentTypeName,
            Long baseId,
            String baseName,
            String personnelName,
            String personnelIdentifier,
            Integer quantity,
            LocalDate assignmentDate,
            String status,
            String notes,
            String createdByUsername,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.assetId = assetId;
        this.assetName = assetName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentTypeName = equipmentTypeName;
        this.baseId = baseId;
        this.baseName = baseName;
        this.personnelName = personnelName;
        this.personnelIdentifier = personnelIdentifier;
        this.quantity = quantity;
        this.assignmentDate = assignmentDate;
        this.status = status;
        this.notes = notes;
        this.createdByUsername = createdByUsername;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long assetId;
        private String assetName;
        private Long equipmentTypeId;
        private String equipmentTypeName;
        private Long baseId;
        private String baseName;
        private String personnelName;
        private String personnelIdentifier;
        private Integer quantity;
        private LocalDate assignmentDate;
        private String status;
        private String notes;
        private String createdByUsername;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder assetId(Long assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder assetName(String assetName) {
            this.assetName = assetName;
            return this;
        }

        public Builder equipmentTypeId(Long equipmentTypeId) {
            this.equipmentTypeId = equipmentTypeId;
            return this;
        }

        public Builder equipmentTypeName(String equipmentTypeName) {
            this.equipmentTypeName = equipmentTypeName;
            return this;
        }

        public Builder baseId(Long baseId) {
            this.baseId = baseId;
            return this;
        }

        public Builder baseName(String baseName) {
            this.baseName = baseName;
            return this;
        }

        public Builder personnelName(String personnelName) {
            this.personnelName = personnelName;
            return this;
        }

        public Builder personnelIdentifier(String personnelIdentifier) {
            this.personnelIdentifier = personnelIdentifier;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder assignmentDate(LocalDate assignmentDate) {
            this.assignmentDate = assignmentDate;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdByUsername(String createdByUsername) {
            this.createdByUsername = createdByUsername;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AssignmentResponse build() {
            return new AssignmentResponse(
                    id, assetId, assetName, equipmentTypeId, equipmentTypeName,
                    baseId, baseName, personnelName, personnelIdentifier,
                    quantity, assignmentDate, status, notes,
                    createdByUsername, createdAt
            );
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    public String getEquipmentTypeName() {
        return equipmentTypeName;
    }

    public void setEquipmentTypeName(String equipmentTypeName) {
        this.equipmentTypeName = equipmentTypeName;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getPersonnelName() {
        return personnelName;
    }

    public void setPersonnelName(String personnelName) {
        this.personnelName = personnelName;
    }

    public String getPersonnelIdentifier() {
        return personnelIdentifier;
    }

    public void setPersonnelIdentifier(String personnelIdentifier) {
        this.personnelIdentifier = personnelIdentifier;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AssignmentResponse{" +
                "id=" + id +
                ", assetId=" + assetId +
                ", assetName='" + assetName + '\'' +
                ", equipmentTypeId=" + equipmentTypeId +
                ", equipmentTypeName='" + equipmentTypeName + '\'' +
                ", baseId=" + baseId +
                ", baseName='" + baseName + '\'' +
                ", personnelName='" + personnelName + '\'' +
                ", personnelIdentifier='" + personnelIdentifier + '\'' +
                ", quantity=" + quantity +
                ", assignmentDate=" + assignmentDate +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                ", createdByUsername='" + createdByUsername + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
