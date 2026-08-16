package com.example.complaintmanagement.dto;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private Long complaintId;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    // Constructors
    public NotificationResponse() {}

    public NotificationResponse(Long id, Long complaintId, String message, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.complaintId = complaintId;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static NotificationResponseBuilder builder() {
        return new NotificationResponseBuilder();
    }

    public static class NotificationResponseBuilder {
        private Long id;
        private Long complaintId;
        private String message;
        private boolean read;
        private LocalDateTime createdAt;

        public NotificationResponseBuilder id(Long id) { this.id = id; return this; }
        public NotificationResponseBuilder complaintId(Long complaintId) { this.complaintId = complaintId; return this; }
        public NotificationResponseBuilder message(String message) { this.message = message; return this; }
        public NotificationResponseBuilder read(boolean read) { this.read = read; return this; }
        public NotificationResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public NotificationResponse build() {
            return new NotificationResponse(id, complaintId, message, read, createdAt);
        }
    }
}
