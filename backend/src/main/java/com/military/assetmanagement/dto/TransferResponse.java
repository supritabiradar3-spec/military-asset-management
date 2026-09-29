package com.military.assetmanagement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransferResponse {

    private Long id;
    private Long assetId;
    private String assetName;
    private Long equipmentTypeId;
    private String equipmentTypeName;
    private Long fromBaseId;
    private String fromBaseName;
    private Long toBaseId;
    private String toBaseName;
    private Integer quantity;
    private LocalDate transferDate;
    private String reason;
    private String createdByUsername;
    private LocalDateTime createdAt;

    public TransferResponse() {
    }

    public TransferResponse(
            Long id,
            Long assetId,
            String assetName,
            Long equipmentTypeId,
            String equipmentTypeName,
            Long fromBaseId,
            String fromBaseName,
            Long toBaseId,
            String toBaseName,
            Integer quantity,
            LocalDate transferDate,
            String reason,
            String createdByUsername,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.assetId = assetId;
        this.assetName = assetName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentTypeName = equipmentTypeName;
        this.fromBaseId = fromBaseId;
        this.fromBaseName = fromBaseName;
        this.toBaseId = toBaseId;
        this.toBaseName = toBaseName;
        this.quantity = quantity;
        this.transferDate = transferDate;
        this.reason = reason;
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
        private Long fromBaseId;
        private String fromBaseName;
        private Long toBaseId;
        private String toBaseName;
        private Integer quantity;
        private LocalDate transferDate;
        private String reason;
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

        public Builder fromBaseId(Long fromBaseId) {
            this.fromBaseId = fromBaseId;
            return this;
        }

        public Builder fromBaseName(String fromBaseName) {
            this.fromBaseName = fromBaseName;
            return this;
        }

        public Builder toBaseId(Long toBaseId) {
            this.toBaseId = toBaseId;
            return this;
        }

        public Builder toBaseName(String toBaseName) {
            this.toBaseName = toBaseName;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder transferDate(LocalDate transferDate) {
            this.transferDate = transferDate;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
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

        public TransferResponse build() {
            return new TransferResponse(
                    id, assetId, assetName, equipmentTypeId, equipmentTypeName,
                    fromBaseId, fromBaseName, toBaseId, toBaseName,
                    quantity, transferDate, reason,
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

    public Long getFromBaseId() {
        return fromBaseId;
    }

    public void setFromBaseId(Long fromBaseId) {
        this.fromBaseId = fromBaseId;
    }

    public String getFromBaseName() {
        return fromBaseName;
    }

    public void setFromBaseName(String fromBaseName) {
        this.fromBaseName = fromBaseName;
    }

    public Long getToBaseId() {
        return toBaseId;
    }

    public void setToBaseId(Long toBaseId) {
        this.toBaseId = toBaseId;
    }

    public String getToBaseName() {
        return toBaseName;
    }

    public void setToBaseName(String toBaseName) {
        this.toBaseName = toBaseName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
        return "TransferResponse{" +
                "id=" + id +
                ", assetId=" + assetId +
                ", assetName='" + assetName + '\'' +
                ", equipmentTypeId=" + equipmentTypeId +
                ", equipmentTypeName='" + equipmentTypeName + '\'' +
                ", fromBaseId=" + fromBaseId +
                ", fromBaseName='" + fromBaseName + '\'' +
                ", toBaseId=" + toBaseId +
                ", toBaseName='" + toBaseName + '\'' +
                ", quantity=" + quantity +
                ", transferDate=" + transferDate +
                ", reason='" + reason + '\'' +
                ", createdByUsername='" + createdByUsername + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
