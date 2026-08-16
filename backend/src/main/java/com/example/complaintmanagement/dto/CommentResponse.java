package com.example.complaintmanagement.dto;

import java.time.LocalDateTime;

public class CommentResponse {
    private Long id;
    private Long complaintId;
    private UserResponse user;
    private String comment;
    private LocalDateTime createdAt;

    // Constructors
    public CommentResponse() {}

    public CommentResponse(Long id, Long complaintId, UserResponse user, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.complaintId = complaintId;
        this.user = user;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static CommentResponseBuilder builder() {
        return new CommentResponseBuilder();
    }

    public static class CommentResponseBuilder {
        private Long id;
        private Long complaintId;
        private UserResponse user;
        private String comment;
        private LocalDateTime createdAt;

        public CommentResponseBuilder id(Long id) { this.id = id; return this; }
        public CommentResponseBuilder complaintId(Long complaintId) { this.complaintId = complaintId; return this; }
        public CommentResponseBuilder user(UserResponse user) { this.user = user; return this; }
        public CommentResponseBuilder comment(String comment) { this.comment = comment; return this; }
        public CommentResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public CommentResponse build() {
            return new CommentResponse(id, complaintId, user, comment, createdAt);
        }
    }
}
