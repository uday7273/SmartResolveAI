package com.example.complaintmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateComplaintRequest {

    @NotBlank(message = "Complaint title cannot be empty")
    @Size(max = 150, message = "Complaint title must be less than 150 characters")
    private String title;

    @NotBlank(message = "Complaint description cannot be empty")
    private String description;

    // Constructors
    public CreateComplaintRequest() {}

    public CreateComplaintRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
