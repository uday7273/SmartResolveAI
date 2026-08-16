package com.example.complaintmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequest {
    
    @NotBlank(message = "Comment cannot be empty")
    private String comment;

    // Constructors
    public CommentRequest() {}

    public CommentRequest(String comment) {
        this.comment = comment;
    }

    // Getters and Setters
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
