package com.military.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EquipmentTypeRequest {

    @NotBlank(message = "Equipment type name is required")
    @Size(max = 100, message = "Equipment type name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    public EquipmentTypeRequest() {
    }

    public EquipmentTypeRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String description;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public EquipmentTypeRequest build() {
            return new EquipmentTypeRequest(name, description);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EquipmentTypeRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
