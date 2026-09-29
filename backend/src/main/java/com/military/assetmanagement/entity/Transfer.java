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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_base_id", nullable = false)
    private Base fromBase;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_base_id", nullable = false)
    private Base toBase;

    @NotNull
    @Min(1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    @Size(max = 500)
    @Column(name = "reason", length = 500)
    private String reason;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Transfer() {
    }

    public Transfer(Long id, Asset asset, Base fromBase, Base toBase, Integer quantity, LocalDate transferDate, String reason, User createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.asset = asset;
        this.fromBase = fromBase;
        this.toBase = toBase;
        this.quantity = quantity;
        this.transferDate = transferDate;
        this.reason = reason;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Asset asset;
        private Base fromBase;
        private Base toBase;
        private Integer quantity;
        private LocalDate transferDate;
        private String reason;
        private User createdBy;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder asset(Asset asset) {
            this.asset = asset;
            return this;
        }

        public Builder fromBase(Base fromBase) {
            this.fromBase = fromBase;
            return this;
        }

        public Builder toBase(Base toBase) {
            this.toBase = toBase;
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

        public Builder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Transfer build() {
            return new Transfer(id, asset, fromBase, toBase, quantity, transferDate, reason, createdBy, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (transferDate == null) {
            transferDate = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Base getFromBase() {
        return fromBase;
    }

    public void setFromBase(Base fromBase) {
        this.fromBase = fromBase;
    }

    public Base getToBase() {
        return toBase;
    }

    public void setToBase(Base toBase) {
        this.toBase = toBase;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return id != null && Objects.equals(id, transfer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", transferDate=" + transferDate +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
