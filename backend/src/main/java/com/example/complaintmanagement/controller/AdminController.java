package com.example.complaintmanagement.controller;

import com.example.complaintmanagement.dto.ComplaintResponse;
import com.example.complaintmanagement.dto.DashboardStatisticsResponse;
import com.example.complaintmanagement.dto.UserResponse;
import com.example.complaintmanagement.entity.User;
import com.example.complaintmanagement.enums.Category;
import com.example.complaintmanagement.enums.Priority;
import com.example.complaintmanagement.enums.Role;
import com.example.complaintmanagement.repository.UserRepository;
import com.example.complaintmanagement.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations", description = "Endpoints restricted to administrator users")
public class AdminController {

    private final ComplaintService complaintService;
    private final UserRepository userRepository;

    public AdminController(ComplaintService complaintService, UserRepository userRepository) {
        this.complaintService = complaintService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard statistics", description = "Returns aggregates of total, pending, resolved tickets, category distributions, and average resolution times.")
    public ResponseEntity<DashboardStatisticsResponse> getDashboardStatistics() {
        return ResponseEntity.ok(complaintService.getDashboardStatistics());
    }

    @GetMapping("/complaints")
    @Operation(summary = "Get all complaints with sorting options", description = "Allows sorting by priority, created date, updated date, or status.")
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ResponseEntity.ok(complaintService.getAllComplaints(sortBy, direction));
    }

    @GetMapping("/complaints/prioritized")
    @Operation(summary = "Get priority-queued complaints (DSA)", description = "Retrieves all complaints ordered by priority (CRITICAL > HIGH > MEDIUM > LOW) using a Java PriorityQueue.")
    public ResponseEntity<List<ComplaintResponse>> getPrioritizedComplaints() {
        return ResponseEntity.ok(complaintService.getPrioritizedComplaints());
    }

    @PutMapping("/complaints/{id}/assign")
    @Operation(summary = "Assign ticket to technician", description = "Assigns the complaint to a technician and sets status to ASSIGNED.")
    public ResponseEntity<ComplaintResponse> assignComplaint(
            @PathVariable Long id,
            @RequestParam Long technicianId,
            Principal principal
    ) {
        return ResponseEntity.ok(complaintService.assignComplaint(id, technicianId, principal.getName()));
    }

    @PutMapping("/complaints/{id}/priority")
    @Operation(summary = "Override complaint priority", description = "Changes the severity priority of a complaint manually.")
    public ResponseEntity<ComplaintResponse> updateComplaintPriority(
            @PathVariable Long id,
            @RequestParam Priority priority,
            Principal principal
    ) {
        return ResponseEntity.ok(complaintService.updateComplaintPriority(id, priority, principal.getName()));
    }

    @PutMapping("/complaints/{id}/category")
    @Operation(summary = "Override complaint category", description = "Changes the category of a complaint manually.")
    public ResponseEntity<ComplaintResponse> updateComplaintCategory(
            @PathVariable Long id,
            @RequestParam Category category,
            Principal principal
    ) {
        return ResponseEntity.ok(complaintService.updateComplaintCategory(id, category, principal.getName()));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all registered users", description = "Returns a list of all user profiles registered in the system.")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/technicians")
    @Operation(summary = "Get all technicians", description = "Returns a list of all users registered with the TECHNICIAN role.")
    public ResponseEntity<List<UserResponse>> getAllTechnicians() {
        List<UserResponse> technicians = userRepository.findByRole(Role.TECHNICIAN).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(technicians);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
