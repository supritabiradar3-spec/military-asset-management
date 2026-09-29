package com.military.assetmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class PurchaseRequest {

    @NotNull(message = "Base ID is required")
    private Long baseId;

    private Long assetId;

    private String assetName;

    private Long equipmentTypeId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Purchase quantity must be at least 1")
    private Integer quantity;

    private LocalDate purchaseDate;

    private String supplier;

    private String notes;

    public PurchaseRequest() {
    }

    public PurchaseRequest(Long baseId, Long assetId, String assetName, Long equipmentTypeId, Integer quantity, LocalDate purchaseDate, String supplier, String notes) {
        this.baseId = baseId;
        this.assetId = assetId;
        this.assetName = assetName;
        this.equipmentTypeId = equipmentTypeId;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.supplier = supplier;
        this.notes = notes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long baseId;
        private Long assetId;
        private String assetName;
        private Long equipmentTypeId;
        private Integer quantity;
        private LocalDate purchaseDate;
        private String supplier;
        private String notes;

        public Builder baseId(Long baseId) {
            this.baseId = baseId;
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

        public PurchaseRequest build() {
            return new PurchaseRequest(baseId, assetId, assetName, equipmentTypeId, quantity, purchaseDate, supplier, notes);
        }
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
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

    @Override
    public String toString() {
        return "PurchaseRequest{" +
                "baseId=" + baseId +
                ", assetId=" + assetId +
                ", assetName='" + assetName + '\'' +
                ", equipmentTypeId=" + equipmentTypeId +
                ", quantity=" + quantity +
                ", purchaseDate=" + purchaseDate +
                ", supplier='" + supplier + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
