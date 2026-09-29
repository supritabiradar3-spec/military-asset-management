package com.military.assetmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class TransferRequest {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Source base ID is required")
    private Long fromBaseId;

    @NotNull(message = "Destination base ID is required")
    private Long toBaseId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Transfer quantity must be at least 1")
    private Integer quantity;

    private LocalDate transferDate;

    private String reason;

    public TransferRequest() {
    }

    public TransferRequest(Long assetId, Long fromBaseId, Long toBaseId, Integer quantity, LocalDate transferDate, String reason) {
        this.assetId = assetId;
        this.fromBaseId = fromBaseId;
        this.toBaseId = toBaseId;
        this.quantity = quantity;
        this.transferDate = transferDate;
        this.reason = reason;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long assetId;
        private Long fromBaseId;
        private Long toBaseId;
        private Integer quantity;
        private LocalDate transferDate;
        private String reason;

        public Builder assetId(Long assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder fromBaseId(Long fromBaseId) {
            this.fromBaseId = fromBaseId;
            return this;
        }

        public Builder toBaseId(Long toBaseId) {
            this.toBaseId = toBaseId;
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

        public TransferRequest build() {
            return new TransferRequest(assetId, fromBaseId, toBaseId, quantity, transferDate, reason);
        }
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public Long getFromBaseId() {
        return fromBaseId;
    }

    public void setFromBaseId(Long fromBaseId) {
        this.fromBaseId = fromBaseId;
    }

    public Long getToBaseId() {
        return toBaseId;
    }

    public void setToBaseId(Long toBaseId) {
        this.toBaseId = toBaseId;
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

    @Override
    public String toString() {
        return "TransferRequest{" +
                "assetId=" + assetId +
                ", fromBaseId=" + fromBaseId +
                ", toBaseId=" + toBaseId +
                ", quantity=" + quantity +
                ", transferDate=" + transferDate +
                ", reason='" + reason + '\'' +
                '}';
    }
}
