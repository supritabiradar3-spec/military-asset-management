package com.military.assetmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AssignmentRequest {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotBlank(message = "Personnel name is required")
    private String personnelName;

    private String personnelIdentifier;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Assignment quantity must be at least 1")
    private Integer quantity;

    private LocalDate assignmentDate;

    private String notes;

    public AssignmentRequest() {
    }

    public AssignmentRequest(Long assetId, Long baseId, String personnelName, String personnelIdentifier, Integer quantity, LocalDate assignmentDate, String notes) {
        this.assetId = assetId;
        this.baseId = baseId;
        this.personnelName = personnelName;
        this.personnelIdentifier = personnelIdentifier;
        this.quantity = quantity;
        this.assignmentDate = assignmentDate;
        this.notes = notes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long assetId;
        private Long baseId;
        private String personnelName;
        private String personnelIdentifier;
        private Integer quantity;
        private LocalDate assignmentDate;
        private String notes;

        public Builder assetId(Long assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder baseId(Long baseId) {
            this.baseId = baseId;
            return this;
        }

        public Builder personnelName(String personnelName) {
            this.personnelName = personnelName;
            return this;
        }

        public Builder personnelIdentifier(String personnelIdentifier) {
            this.personnelIdentifier = personnelIdentifier;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder assignmentDate(LocalDate assignmentDate) {
            this.assignmentDate = assignmentDate;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public AssignmentRequest build() {
            return new AssignmentRequest(assetId, baseId, personnelName, personnelIdentifier, quantity, assignmentDate, notes);
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

    public String getPersonnelName() {
        return personnelName;
    }

    public void setPersonnelName(String personnelName) {
        this.personnelName = personnelName;
    }

    public String getPersonnelIdentifier() {
        return personnelIdentifier;
    }

    public void setPersonnelIdentifier(String personnelIdentifier) {
        this.personnelIdentifier = personnelIdentifier;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "AssignmentRequest{" +
                "assetId=" + assetId +
                ", baseId=" + baseId +
                ", personnelName='" + personnelName + '\'' +
                ", personnelIdentifier='" + personnelIdentifier + '\'' +
                ", quantity=" + quantity +
                ", assignmentDate=" + assignmentDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}
