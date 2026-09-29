package com.military.assetmanagement.dto;

public class BaseResponse {

    private Long id;
    private String name;
    private String location;

    public BaseResponse() {
    }

    public BaseResponse(Long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String location;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public BaseResponse build() {
            return new BaseResponse(id, name, location);
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
