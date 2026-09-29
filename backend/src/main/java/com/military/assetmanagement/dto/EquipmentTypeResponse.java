package com.military.assetmanagement.dto;

public class EquipmentTypeResponse {

    private Long id;
    private String name;
    private String description;

    public EquipmentTypeResponse() {
    }

    public EquipmentTypeResponse(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public EquipmentTypeResponse build() {
            return new EquipmentTypeResponse(id, name, description);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EquipmentTypeResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
