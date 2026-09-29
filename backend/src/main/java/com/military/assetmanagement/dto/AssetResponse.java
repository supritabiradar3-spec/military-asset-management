package com.military.assetmanagement.dto;

import java.time.LocalDateTime;

public class AssetResponse {

    private Long id;
    private String name;
    private Long equipmentTypeId;
    private String equipmentTypeName;
    private Long baseId;
    private String baseName;
    private Integer quantity;
    private Integer assignedQuantity;
    private Integer availableQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssetResponse() {
    }

    public AssetResponse(
            Long id,
            String name,
            Long equipmentTypeId,
            String equipmentTypeName,
            Long baseId,
            String baseName,
            Integer quantity,
            Integer assignedQuantity,
            Integer availableQuantity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentTypeName = equipmentTypeName;
        this.baseId = baseId;
        this.baseName = baseName;
        this.quantity = quantity;
        this.assignedQuantity = assignedQuantity;
        this.availableQuantity = availableQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private Long equipmentTypeId;
        private String equipmentTypeName;
        private Long baseId;
        private String baseName;
        private Integer quantity;
        private Integer assignedQuantity;
        private Integer availableQuantity;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
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

        public Builder assignedQuantity(Integer assignedQuantity) {
            this.assignedQuantity = assignedQuantity;
            return this;
        }

        public Builder availableQuantity(Integer availableQuantity) {
            this.availableQuantity = availableQuantity;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AssetResponse build() {
            return new AssetResponse(
                    id, name, equipmentTypeId, equipmentTypeName,
                    baseId, baseName, quantity, assignedQuantity,
                    availableQuantity, createdAt, updatedAt
            );
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getAssignedQuantity() {
        return assignedQuantity;
    }

    public void setAssignedQuantity(Integer assignedQuantity) {
        this.assignedQuantity = assignedQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "AssetResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", equipmentTypeId=" + equipmentTypeId +
                ", equipmentTypeName='" + equipmentTypeName + '\'' +
                ", baseId=" + baseId +
                ", baseName='" + baseName + '\'' +
                ", quantity=" + quantity +
                ", assignedQuantity=" + assignedQuantity +
                ", availableQuantity=" + availableQuantity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
