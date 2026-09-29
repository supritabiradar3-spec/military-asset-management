package com.military.assetmanagement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpenditureResponse {

    private Long id;
    private Long assetId;
    private String assetName;
    private Long equipmentTypeId;
    private String equipmentTypeName;
    private Long baseId;
    private String baseName;
    private Integer quantity;
    private String reason;
    private LocalDate expenditureDate;
    private String createdByUsername;
    private LocalDateTime createdAt;

    public ExpenditureResponse() {
    }

    public ExpenditureResponse(
            Long id,
            Long assetId,
            String assetName,
            Long equipmentTypeId,
            String equipmentTypeName,
            Long baseId,
            String baseName,
            Integer quantity,
            String reason,
            LocalDate expenditureDate,
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
        this.quantity = quantity;
        this.reason = reason;
        this.expenditureDate = expenditureDate;
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
        private Integer quantity;
        private String reason;
        private LocalDate expenditureDate;
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

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder expenditureDate(LocalDate expenditureDate) {
            this.expenditureDate = expenditureDate;
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

        public ExpenditureResponse build() {
            return new ExpenditureResponse(
                    id, assetId, assetName, equipmentTypeId, equipmentTypeName,
                    baseId, baseName, quantity, reason, expenditureDate,
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getExpenditureDate() {
        return expenditureDate;
    }

    public void setExpenditureDate(LocalDate expenditureDate) {
        this.expenditureDate = expenditureDate;
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
        return "ExpenditureResponse{" +
                "id=" + id +
                ", assetId=" + assetId +
                ", assetName='" + assetName + '\'' +
                ", equipmentTypeId=" + equipmentTypeId +
                ", equipmentTypeName='" + equipmentTypeName + '\'' +
                ", baseId=" + baseId +
                ", baseName='" + baseName + '\'' +
                ", quantity=" + quantity +
                ", reason='" + reason + '\'' +
                ", expenditureDate=" + expenditureDate +
                ", createdByUsername='" + createdByUsername + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
