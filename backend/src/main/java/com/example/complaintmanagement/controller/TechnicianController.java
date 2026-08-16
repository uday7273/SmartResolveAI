package com.example.complaintmanagement.controller;

import com.example.complaintmanagement.dto.ComplaintResponse;
import com.example.complaintmanagement.enums.Status;
import com.example.complaintmanagement.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/technician")
@PreAuthorize("hasRole('TECHNICIAN')")
@Tag(name = "Technician Operations", description = "Endpoints restricted to technician/staff users")
public class TechnicianController {

    private final ComplaintService complaintService;

    public TechnicianController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/complaints")
    @Operation(summary = "Get assigned complaints", description = "Retrieves all complaints assigned to the logged-in technician.")
    public ResponseEntity<List<ComplaintResponse>> getAssignedComplaints(Principal principal) {
        return ResponseEntity.ok(complaintService.getComplaintsForTechnician(principal.getName()));
    }

    @PutMapping("/complaints/{id}/status")
    @Operation(summary = "Update complaint status", description = "Updates status of assigned complaint (valid transitions: IN_PROGRESS, RESOLVED).")
    public ResponseEntity<ComplaintResponse> updateComplaintStatus(
            @PathVariable Long id,
            @RequestParam Status status,
            Principal principal
    ) {
        return ResponseEntity.ok(complaintService.updateComplaintStatus(id, status, principal.getName()));
    }
}
