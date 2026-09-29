package com.military.assetmanagement.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;
    private String username;
    private String action;
    private String entityType;
    private Long entityId;
    private Long baseId;
    private String details;
    private LocalDateTime timestamp;

    public AuditLogResponse() {
    }

    public AuditLogResponse(Long id, String username, String action, String entityType, Long entityId, Long baseId, String details, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.baseId = baseId;
        this.details = details;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String username;
        private String action;
        private String entityType;
        private Long entityId;
        private Long baseId;
        private String details;
        private LocalDateTime timestamp;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder entityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder baseId(Long baseId) {
            this.baseId = baseId;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public AuditLogResponse build() {
            return new AuditLogResponse(id, username, action, entityType, entityId, baseId, details, timestamp);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AuditLogResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", action='" + action + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                ", baseId=" + baseId +
                ", details='" + details + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
