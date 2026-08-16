package com.example.complaintmanagement.dto;

import com.example.complaintmanagement.enums.Category;
import com.example.complaintmanagement.enums.Priority;
import com.example.complaintmanagement.enums.Status;

public class UpdateComplaintRequest {
    private Status status;
    private Priority priority;
    private Category category;
    private Long assignedTo;

    // Constructors
    public UpdateComplaintRequest() {}

    public UpdateComplaintRequest(Status status, Priority priority, Category category, Long assignedTo) {
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.assignedTo = assignedTo;
    }

    // Getters and Setters
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Long getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }
}
