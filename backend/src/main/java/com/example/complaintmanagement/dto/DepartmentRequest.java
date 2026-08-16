package com.example.complaintmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class DepartmentRequest {
    @NotBlank(message = "Department name is required")
    private String name;
    private String description;

    // Constructors
    public DepartmentRequest() {}

    public DepartmentRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
