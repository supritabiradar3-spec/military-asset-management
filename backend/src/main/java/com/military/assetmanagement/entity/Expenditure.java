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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "expenditures")
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @NotNull
    @Min(1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotBlank
    @Size(max = 255)
    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @NotNull
    @Column(name = "expenditure_date", nullable = false)
    private LocalDate expenditureDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Expenditure() {
    }

    public Expenditure(Long id, Asset asset, Base base, Integer quantity, String reason, LocalDate expenditureDate, User createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.asset = asset;
        this.base = base;
        this.quantity = quantity;
        this.reason = reason;
        this.expenditureDate = expenditureDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Asset asset;
        private Base base;
        private Integer quantity;
        private String reason;
        private LocalDate expenditureDate;
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

        public Builder base(Base base) {
            this.base = base;
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

        public Builder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Expenditure build() {
            return new Expenditure(id, asset, base, quantity, reason, expenditureDate, createdBy, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (expenditureDate == null) {
            expenditureDate = LocalDate.now();
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
        Expenditure that = (Expenditure) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Expenditure{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", reason='" + reason + '\'' +
                ", expenditureDate=" + expenditureDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
