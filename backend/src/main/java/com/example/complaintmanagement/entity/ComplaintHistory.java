package com.example.complaintmanagement.entity;

import com.example.complaintmanagement.enums.Status;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_history")
public class ComplaintHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private Status oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private Status newStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }

    // Constructors
    public ComplaintHistory() {}

    public ComplaintHistory(Long id, Complaint complaint, Status oldStatus, Status newStatus, User changedBy, LocalDateTime changedAt) {
        this.id = id;
        this.complaint = complaint;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }

    public Status getOldStatus() { return oldStatus; }
    public void setOldStatus(Status oldStatus) { this.oldStatus = oldStatus; }

    public Status getNewStatus() { return newStatus; }
    public void setNewStatus(Status newStatus) { this.newStatus = newStatus; }

    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User changedBy) { this.changedBy = changedBy; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    // Builder
    public static ComplaintHistoryBuilder builder() {
        return new ComplaintHistoryBuilder();
    }

    public static class ComplaintHistoryBuilder {
        private Long id;
        private Complaint complaint;
        private Status oldStatus;
        private Status newStatus;
        private User changedBy;
        private LocalDateTime changedAt;

        public ComplaintHistoryBuilder id(Long id) { this.id = id; return this; }
        public ComplaintHistoryBuilder complaint(Complaint complaint) { this.complaint = complaint; return this; }
        public ComplaintHistoryBuilder oldStatus(Status oldStatus) { this.oldStatus = oldStatus; return this; }
        public ComplaintHistoryBuilder newStatus(Status newStatus) { this.newStatus = newStatus; return this; }
        public ComplaintHistoryBuilder changedBy(User changedBy) { this.changedBy = changedBy; return this; }
        public ComplaintHistoryBuilder changedAt(LocalDateTime changedAt) { this.changedAt = changedAt; return this; }

        public ComplaintHistory build() {
            return new ComplaintHistory(id, complaint, oldStatus, newStatus, changedBy, changedAt);
        }
    }
}
