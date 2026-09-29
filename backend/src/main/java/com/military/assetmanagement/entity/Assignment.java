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
@Table(name = "assignments")
public class Assignment {

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

    @NotBlank
    @Size(max = 100)
    @Column(name = "personnel_name", nullable = false, length = 100)
    private String personnelName;

    @Size(max = 50)
    @Column(name = "personnel_identifier", length = 50)
    private String personnelIdentifier;

    @NotNull
    @Min(1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Size(max = 500)
    @Column(name = "notes", length = 500)
    private String notes;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Assignment() {
    }

    public Assignment(Long id, Asset asset, Base base, String personnelName, String personnelIdentifier, Integer quantity, LocalDate assignmentDate, String status, String notes, User createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.asset = asset;
        this.base = base;
        this.personnelName = personnelName;
        this.personnelIdentifier = personnelIdentifier;
        this.quantity = quantity;
        this.assignmentDate = assignmentDate;
        this.status = status != null ? status : "ACTIVE";
        this.notes = notes;
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
        private String personnelName;
        private String personnelIdentifier;
        private Integer quantity;
        private LocalDate assignmentDate;
        private String status = "ACTIVE";
        private String notes;
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

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
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

        public Assignment build() {
            return new Assignment(id, asset, base, personnelName, personnelIdentifier, quantity, assignmentDate, status, notes, createdBy, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (assignmentDate == null) {
            assignmentDate = LocalDate.now();
        }
        if (status == null) {
            status = "ACTIVE";
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        Assignment that = (Assignment) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", personnelName='" + personnelName + '\'' +
                ", personnelIdentifier='" + personnelIdentifier + '\'' +
                ", quantity=" + quantity +
                ", assignmentDate=" + assignmentDate +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
