package com.example.complaintmanagement.controller;

import com.example.complaintmanagement.dto.*;
import com.example.complaintmanagement.entity.ComplaintHistory;
import com.example.complaintmanagement.enums.Role;
import com.example.complaintmanagement.enums.Status;
import com.example.complaintmanagement.repository.UserRepository;
import com.example.complaintmanagement.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@Tag(name = "Complaints", description = "Endpoints for creating and tracking complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserRepository userRepository;

    public ComplaintController(ComplaintService complaintService, UserRepository userRepository) {
        this.complaintService = complaintService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create complaint (User)", description = "Allows a USER to raise a new service complaint. Triggers AI analysis.")
    public ResponseEntity<ComplaintResponse> createComplaint(@Valid @RequestBody CreateComplaintRequest request, Principal principal) {
        return new ResponseEntity<>(complaintService.createComplaint(request, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get complaints by role context", description = "Returns complaints based on role (User gets own, Tech gets assigned, Admin gets all).")
    public ResponseEntity<List<ComplaintResponse>> getComplaints(Principal principal) {
        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));

        if (user.getRole() == Role.ADMIN) {
            return ResponseEntity.ok(complaintService.getAllComplaints("createdAt", "desc"));
        } else if (user.getRole() == Role.TECHNICIAN) {
            return ResponseEntity.ok(complaintService.getComplaintsForTechnician(principal.getName()));
        } else {
            return ResponseEntity.ok(complaintService.getComplaintsForUser(principal.getName()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complaint details", description = "Retrieves complete details of a specific complaint. Securely checked against role permissions.")
    public ResponseEntity<ComplaintResponse> getComplaintById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(complaintService.getComplaintById(id, principal.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update complaint status (Role Specific)", description = "Allows user to close their resolved complaints. Techs/Admins can perform other transitions.")
    public ResponseEntity<ComplaintResponse> updateComplaintStatus(
            @PathVariable Long id, 
            @RequestParam Status status, 
            Principal principal
    ) {
        return ResponseEntity.ok(complaintService.updateComplaintStatus(id, status, principal.getName()));
    }

    // Comments Endpoints
    @PostMapping("/{id}/comments")
    @Operation(summary = "Add comment to a complaint", description = "Allows stakeholders to post a text comment to a ticket thread.")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id, 
            @Valid @RequestBody CommentRequest request, 
            Principal principal
    ) {
        return new ResponseEntity<>(complaintService.addComment(id, request, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Get comments thread", description = "Returns all comments posted to a complaint.")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(complaintService.getCommentsForComplaint(id, principal.getName()));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get status change logs", description = "Returns audit history for a complaint's status changes.")
    public ResponseEntity<List<ComplaintHistory>> getHistory(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(complaintService.getComplaintHistory(id, principal.getName()));
    }
}
