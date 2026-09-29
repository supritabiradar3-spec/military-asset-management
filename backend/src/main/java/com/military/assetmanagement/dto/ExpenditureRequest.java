package com.military.assetmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ExpenditureRequest {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Expenditure quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "Reason for expenditure is required")
    private String reason;

    private LocalDate expenditureDate;

    public ExpenditureRequest() {
    }

    public ExpenditureRequest(Long assetId, Long baseId, Integer quantity, String reason, LocalDate expenditureDate) {
        this.assetId = assetId;
        this.baseId = baseId;
        this.quantity = quantity;
        this.reason = reason;
        this.expenditureDate = expenditureDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long assetId;
        private Long baseId;
        private Integer quantity;
        private String reason;
        private LocalDate expenditureDate;

        public Builder assetId(Long assetId) {
            this.assetId = assetId;
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

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder expenditureDate(LocalDate expenditureDate) {
            this.expenditureDate = expenditureDate;
            return this;
        }

        public ExpenditureRequest build() {
            return new ExpenditureRequest(assetId, baseId, quantity, reason, expenditureDate);
        }
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
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

    @Override
    public String toString() {
        return "ExpenditureRequest{" +
                "assetId=" + assetId +
                ", baseId=" + baseId +
                ", quantity=" + quantity +
                ", reason='" + reason + '\'' +
                ", expenditureDate=" + expenditureDate +
                '}';
    }
}
