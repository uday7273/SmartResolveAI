package com.example.complaintmanagement.controller;

import com.example.complaintmanagement.dto.DepartmentRequest;
import com.example.complaintmanagement.entity.Department;
import com.example.complaintmanagement.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Departments", description = "Endpoints for managing departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @Operation(summary = "Get all departments", description = "Returns a list of all departments.")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Returns details of a specific department.")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a department (Admin)", description = "Creates a new department.")
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return new ResponseEntity<>(departmentService.createDepartment(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a department (Admin)", description = "Updates details of an existing department.")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a department (Admin)", description = "Deletes a department from the system.")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
