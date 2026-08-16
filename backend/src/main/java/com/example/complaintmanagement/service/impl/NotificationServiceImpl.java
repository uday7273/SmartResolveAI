package com.example.complaintmanagement.service.impl;

import com.example.complaintmanagement.dto.NotificationResponse;
import com.example.complaintmanagement.entity.Complaint;
import com.example.complaintmanagement.entity.Notification;
import com.example.complaintmanagement.entity.User;
import com.example.complaintmanagement.exception.ResourceNotFoundException;
import com.example.complaintmanagement.repository.NotificationRepository;
import com.example.complaintmanagement.repository.UserRepository;
import com.example.complaintmanagement.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createNotification(User user, Complaint complaint, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .complaint(complaint)
                .message(message)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, String email) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getEmail().equals(email)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to access this notification.");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return notificationRepository.countByUserIdAndReadFalse(user.getId());
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .complaintId(notification.getComplaint().getId())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
