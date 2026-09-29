package com.military.assetmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "assets",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_asset_name_base", columnNames = {"name", "base_id"})
    }
)
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @NotNull
    @Min(0)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Min(0)
    @Column(name = "assigned_quantity", nullable = false)
    private Integer assignedQuantity = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Asset() {
    }

    public Asset(Long id, String name, EquipmentType equipmentType, Base base, Integer quantity, Integer assignedQuantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.equipmentType = equipmentType;
        this.base = base;
        this.quantity = quantity;
        this.assignedQuantity = assignedQuantity != null ? assignedQuantity : 0;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private EquipmentType equipmentType;
        private Base base;
        private Integer quantity;
        private Integer assignedQuantity = 0;
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

        public Builder equipmentType(EquipmentType equipmentType) {
            this.equipmentType = equipmentType;
            return this;
        }

        public Builder base(Base base) {
            this.base = base;
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Asset build() {
            return new Asset(id, name, equipmentType, base, quantity, assignedQuantity, createdAt, updatedAt);
        }
    }

    public int getAvailableQuantity() {
        int total = quantity != null ? quantity : 0;
        int assigned = assignedQuantity != null ? assignedQuantity : 0;
        return Math.max(0, total - assigned);
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (assignedQuantity == null) {
            assignedQuantity = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return id != null && Objects.equals(id, asset.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", assignedQuantity=" + assignedQuantity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
