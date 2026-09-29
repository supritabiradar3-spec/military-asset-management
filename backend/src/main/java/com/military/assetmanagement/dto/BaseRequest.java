package com.military.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BaseRequest {

    @NotBlank(message = "Base name is required")
    @Size(max = 100, message = "Base name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location cannot exceed 255 characters")
    private String location;

    public BaseRequest() {
    }

    public BaseRequest(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String location;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public BaseRequest build() {
            return new BaseRequest(name, location);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "BaseRequest{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
