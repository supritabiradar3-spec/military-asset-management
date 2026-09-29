package com.military.assetmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AssetRequest {

    @NotBlank(message = "Asset name is required")
    @Size(max = 100, message = "Asset name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Equipment type ID is required")
    private Long equipmentTypeId;

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    public AssetRequest() {
    }

    public AssetRequest(String name, Long equipmentTypeId, Long baseId, Integer quantity) {
        this.name = name;
        this.equipmentTypeId = equipmentTypeId;
        this.baseId = baseId;
        this.quantity = quantity;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private Long equipmentTypeId;
        private Long baseId;
        private Integer quantity;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder equipmentTypeId(Long equipmentTypeId) {
            this.equipmentTypeId = equipmentTypeId;
            return this;
        }

        public Builder baseId(Long baseId) {
            this.baseId = baseId;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public AssetRequest build() {
            return new AssetRequest(name, equipmentTypeId, baseId, quantity);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "AssetRequest{" +
                "name='" + name + '\'' +
                ", equipmentTypeId=" + equipmentTypeId +
                ", baseId=" + baseId +
                ", quantity=" + quantity +
                '}';
    }
}
