package com.example.complaintmanagement.entity;

import com.example.complaintmanagement.enums.Category;
import com.example.complaintmanagement.enums.Priority;
import com.example.complaintmanagement.enums.Status;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "ai_suggested_response", columnDefinition = "TEXT")
    private String aiSuggestedResponse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.OPEN;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Complaint() {}

    public Complaint(Long id, String title, String description, Category category, Priority priority, Status status,
                     String aiSummary, String aiSuggestedResponse, User createdBy, User assignedTo,
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

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    // Builder
    public static ComplaintBuilder builder() {
        return new ComplaintBuilder();
    }

    public static class ComplaintBuilder {
        private Long id;
        private String title;
        private String description;
        private Category category;
        private Priority priority;
        private Status status;
        private String aiSummary;
        private String aiSuggestedResponse;
        private User createdBy;
        private User assignedTo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime resolvedAt;

        public ComplaintBuilder id(Long id) { this.id = id; return this; }
        public ComplaintBuilder title(String title) { this.title = title; return this; }
        public ComplaintBuilder description(String description) { this.description = description; return this; }
        public ComplaintBuilder category(Category category) { this.category = category; return this; }
        public ComplaintBuilder priority(Priority priority) { this.priority = priority; return this; }
        public ComplaintBuilder status(Status status) { this.status = status; return this; }
        public ComplaintBuilder aiSummary(String aiSummary) { this.aiSummary = aiSummary; return this; }
        public ComplaintBuilder aiSuggestedResponse(String aiSuggestedResponse) { this.aiSuggestedResponse = aiSuggestedResponse; return this; }
        public ComplaintBuilder createdBy(User createdBy) { this.createdBy = createdBy; return this; }
        public ComplaintBuilder assignedTo(User assignedTo) { this.assignedTo = assignedTo; return this; }
        public ComplaintBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ComplaintBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public ComplaintBuilder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }

        public Complaint build() {
            return new Complaint(id, title, description, category, priority, status, aiSummary, aiSuggestedResponse, createdBy, assignedTo, createdAt, updatedAt, resolvedAt);
        }
    }
}
