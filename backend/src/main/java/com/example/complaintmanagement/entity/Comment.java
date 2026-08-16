package com.example.complaintmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors
    public Comment() {}

    public Comment(Long id, Complaint complaint, User user, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.complaint = complaint;
        this.user = user;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static CommentBuilder builder() {
        return new CommentBuilder();
    }

    public static class CommentBuilder {
        private Long id;
        private Complaint complaint;
        private User user;
        private String comment;
        private LocalDateTime createdAt;

        public CommentBuilder id(Long id) { this.id = id; return this; }
        public CommentBuilder complaint(Complaint complaint) { this.complaint = complaint; return this; }
        public CommentBuilder user(User user) { this.user = user; return this; }
        public CommentBuilder comment(String comment) { this.comment = comment; return this; }
        public CommentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Comment build() {
            return new Comment(id, complaint, user, comment, createdAt);
        }
    }
}
