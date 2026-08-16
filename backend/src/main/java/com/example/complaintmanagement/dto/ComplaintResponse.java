package com.example.complaintmanagement.dto;

import com.example.complaintmanagement.enums.Category;
import com.example.complaintmanagement.enums.Priority;
import com.example.complaintmanagement.enums.Status;
import java.time.LocalDateTime;

public class ComplaintResponse {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private Status status;
    private String aiSummary;
    private String aiSuggestedResponse;
    private UserResponse createdBy;
    private UserResponse assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    // Constructors
    public ComplaintResponse() {}

    public ComplaintResponse(Long id, String title, String description, Category category, Priority priority, Status status,
                             String aiSummary, String aiSuggestedResponse, UserResponse createdBy, UserResponse assignedTo,
                             LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.aiSummary = aiSummary;
        this.aiSuggestedResponse = aiSuggestedResponse;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public String getAiSuggestedResponse() { return aiSuggestedResponse; }
    public void setAiSuggestedResponse(String aiSuggestedResponse) { this.aiSuggestedResponse = aiSuggestedResponse; }

    public UserResponse getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserResponse createdBy) { this.createdBy = createdBy; }

    public UserResponse getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UserResponse assignedTo) { this.assignedTo = assignedTo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    // Builder
    public static ComplaintResponseBuilder builder() {
        return new ComplaintResponseBuilder();
    }

    public static class ComplaintResponseBuilder {
        private Long id;
        private String title;
        private String description;
        private Category category;
        private Priority priority;
        private Status status;
        private String aiSummary;
        private String aiSuggestedResponse;
        private UserResponse createdBy;
        private UserResponse assignedTo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime resolvedAt;

        public ComplaintResponseBuilder id(Long id) { this.id = id; return this; }
        public ComplaintResponseBuilder title(String title) { this.title = title; return this; }
        public ComplaintResponseBuilder description(String description) { this.description = description; return this; }
        public ComplaintResponseBuilder category(Category category) { this.category = category; return this; }
        public ComplaintResponseBuilder priority(Priority priority) { this.priority = priority; return this; }
        public ComplaintResponseBuilder status(Status status) { this.status = status; return this; }
        public ComplaintResponseBuilder aiSummary(String aiSummary) { this.aiSummary = aiSummary; return this; }
        public ComplaintResponseBuilder aiSuggestedResponse(String aiSuggestedResponse) { this.aiSuggestedResponse = aiSuggestedResponse; return this; }
        public ComplaintResponseBuilder createdBy(UserResponse createdBy) { this.createdBy = createdBy; return this; }
        public ComplaintResponseBuilder assignedTo(UserResponse assignedTo) { this.assignedTo = assignedTo; return this; }
        public ComplaintResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ComplaintResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public ComplaintResponseBuilder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }

        public ComplaintResponse build() {
            return new ComplaintResponse(id, title, description, category, priority, status, aiSummary, aiSuggestedResponse, createdBy, assignedTo, createdAt, updatedAt, resolvedAt);
        }
    }
}
