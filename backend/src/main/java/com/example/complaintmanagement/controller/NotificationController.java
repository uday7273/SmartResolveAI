package com.example.complaintmanagement.controller;

import com.example.complaintmanagement.dto.NotificationResponse;
import com.example.complaintmanagement.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Endpoints for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Returns a list of notifications for the authenticated user.")
    public ResponseEntity<List<NotificationResponse>> getNotifications(Principal principal) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(principal.getName()));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read, checking security permissions.")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        notificationService.markAsRead(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notifications count", description = "Returns the count of unread notifications for the user.")
    public ResponseEntity<Long> getUnreadCount(Principal principal) {
        return ResponseEntity.ok(notificationService.countUnread(principal.getName()));
    }
}
