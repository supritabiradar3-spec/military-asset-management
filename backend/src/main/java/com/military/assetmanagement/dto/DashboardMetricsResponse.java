package com.military.assetmanagement.dto;

import java.time.LocalDate;

public class DashboardMetricsResponse {

    private long openingBalance;
    private long purchases;
    private long transferIn;
    private long transferOut;
    private long netMovement;
    private long assigned;
    private long expended;
    private long closingBalance;

    // Filter metadata
    private LocalDate startDate;
    private LocalDate endDate;
    private Long baseId;
    private String baseName;
    private Long equipmentTypeId;
    private String equipmentTypeName;

    public DashboardMetricsResponse() {
    }

    public DashboardMetricsResponse(
            long openingBalance,
            long purchases,
            long transferIn,
            long transferOut,
            long netMovement,
            long assigned,
            long expended,
            long closingBalance,
            LocalDate startDate,
            LocalDate endDate,
            Long baseId,
            String baseName,
            Long equipmentTypeId,
            String equipmentTypeName
    ) {
        this.openingBalance = openingBalance;
        this.purchases = purchases;
        this.transferIn = transferIn;
        this.transferOut = transferOut;
        this.netMovement = netMovement;
        this.assigned = assigned;
        this.expended = expended;
        this.closingBalance = closingBalance;
        this.startDate = startDate;
        this.endDate = endDate;
        this.baseId = baseId;
        this.baseName = baseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentTypeName = equipmentTypeName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long openingBalance;
        private long purchases;
        private long transferIn;
        private long transferOut;
        private long netMovement;
        private long assigned;
        private long expended;
        private long closingBalance;
        private LocalDate startDate;
        private LocalDate endDate;
        private Long baseId;
        private String baseName;
        private Long equipmentTypeId;
        private String equipmentTypeName;

        public Builder openingBalance(long openingBalance) {
            this.openingBalance = openingBalance;
            return this;
        }

        public Builder purchases(long purchases) {
            this.purchases = purchases;
            return this;
        }

        public Builder transferIn(long transferIn) {
            this.transferIn = transferIn;
            return this;
        }

        public Builder transferOut(long transferOut) {
            this.transferOut = transferOut;
            return this;
        }

        public Builder netMovement(long netMovement) {
            this.netMovement = netMovement;
            return this;
        }

        public Builder assigned(long assigned) {
            this.assigned = assigned;
            return this;
        }

        public Builder expended(long expended) {
            this.expended = expended;
            return this;
        }

        public Builder closingBalance(long closingBalance) {
            this.closingBalance = closingBalance;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
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

        public Builder equipmentTypeId(Long equipmentTypeId) {
            this.equipmentTypeId = equipmentTypeId;
            return this;
        }

        public Builder equipmentTypeName(String equipmentTypeName) {
            this.equipmentTypeName = equipmentTypeName;
            return this;
        }

        public DashboardMetricsResponse build() {
            return new DashboardMetricsResponse(
                    openingBalance, purchases, transferIn, transferOut,
                    netMovement, assigned, expended, closingBalance,
                    startDate, endDate, baseId, baseName,
                    equipmentTypeId, equipmentTypeName
            );
        }
    }

    public long getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(long openingBalance) {
        this.openingBalance = openingBalance;
    }

    public long getPurchases() {
        return purchases;
    }

    public void setPurchases(long purchases) {
        this.purchases = purchases;
    }

    public long getTransferIn() {
        return transferIn;
    }

    public void setTransferIn(long transferIn) {
        this.transferIn = transferIn;
    }

    public long getTransferOut() {
        return transferOut;
    }

    public void setTransferOut(long transferOut) {
        this.transferOut = transferOut;
    }

    public long getNetMovement() {
        return netMovement;
    }

    public void setNetMovement(long netMovement) {
        this.netMovement = netMovement;
    }

    public long getAssigned() {
        return assigned;
    }

    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }

    public long getExpended() {
        return expended;
    }

    public void setExpended(long expended) {
        this.expended = expended;
    }

    public long getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(long closingBalance) {
        this.closingBalance = closingBalance;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    @Override
    public String toString() {
        return "DashboardMetricsResponse{" +
                "openingBalance=" + openingBalance +
                ", purchases=" + purchases +
                ", transferIn=" + transferIn +
                ", transferOut=" + transferOut +
                ", netMovement=" + netMovement +
                ", assigned=" + assigned +
                ", expended=" + expended +
                ", closingBalance=" + closingBalance +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", baseId=" + baseId +
                ", baseName='" + baseName + '\'' +
                ", equipmentTypeId=" + equipmentTypeId +
                ", equipmentTypeName='" + equipmentTypeName + '\'' +
                '}';
    }
}
