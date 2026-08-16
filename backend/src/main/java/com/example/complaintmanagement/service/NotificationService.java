package com.example.complaintmanagement.service;

import com.example.complaintmanagement.dto.NotificationResponse;
import com.example.complaintmanagement.entity.Complaint;
import com.example.complaintmanagement.entity.User;
import java.util.List;

public interface NotificationService {
    void createNotification(User user, Complaint complaint, String message);
    List<NotificationResponse> getNotificationsForUser(String email);
    void markAsRead(Long notificationId, String email);
    long countUnread(String email);
}
