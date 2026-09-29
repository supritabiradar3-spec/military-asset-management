package com.military.assetmanagement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseResponse {

    private Long id;
    private Long baseId;
    private String baseName;
    private Long assetId;
    private String assetName;
    private Long equipmentTypeId;
    private String equipmentTypeName;
    private Integer quantity;
    private LocalDate purchaseDate;
    private String supplier;
    private String notes;
    private String createdByUsername;
    private LocalDateTime createdAt;

    public PurchaseResponse() {
    }

    public PurchaseResponse(
            Long id,
            Long baseId,
            String baseName,
            Long assetId,
            String assetName,
            Long equipmentTypeId,
            String equipmentTypeName,
            Integer quantity,
            LocalDate purchaseDate,
            String supplier,
            String notes,
            String createdByUsername,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.baseId = baseId;
        this.baseName = baseName;
        this.assetId = assetId;
        this.assetName = assetName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentTypeName = equipmentTypeName;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.supplier = supplier;
        this.notes = notes;
        this.createdByUsername = createdByUsername;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long baseId;
        private String baseName;
        private Long assetId;
        private String assetName;
        private Long equipmentTypeId;
        private String equipmentTypeName;
        private Integer quantity;
        private LocalDate purchaseDate;
        private String supplier;
        private String notes;
        private String createdByUsername;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
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

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder purchaseDate(LocalDate purchaseDate) {
            this.purchaseDate = purchaseDate;
            return this;
        }

        public Builder supplier(String supplier) {
            this.supplier = supplier;
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

        public PurchaseResponse build() {
            return new PurchaseResponse(
                    id, baseId, baseName, assetId, assetName,
                    equipmentTypeId, equipmentTypeName, quantity,
                    purchaseDate, supplier, notes,
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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
        return "PurchaseResponse{" +
                "id=" + id +
                ", baseId=" + baseId +
                ", baseName='" + baseName + '\'' +
                ", assetId=" + assetId +
                ", assetName='" + assetName + '\'' +
                ", equipmentTypeId=" + equipmentTypeId +
                ", equipmentTypeName='" + equipmentTypeName + '\'' +
                ", quantity=" + quantity +
                ", purchaseDate=" + purchaseDate +
                ", supplier='" + supplier + '\'' +
                ", notes='" + notes + '\'' +
                ", createdByUsername='" + createdByUsername + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
