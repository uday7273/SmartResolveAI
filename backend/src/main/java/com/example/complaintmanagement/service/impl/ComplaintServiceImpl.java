package com.example.complaintmanagement.service.impl;

import com.example.complaintmanagement.ai.AIResponse;
import com.example.complaintmanagement.ai.ComplaintAIService;
import com.example.complaintmanagement.dto.*;
import com.example.complaintmanagement.entity.*;
import com.example.complaintmanagement.enums.Category;
import com.example.complaintmanagement.enums.Priority;
import com.example.complaintmanagement.enums.Role;
import com.example.complaintmanagement.enums.Status;
import com.example.complaintmanagement.exception.BadRequestException;
import com.example.complaintmanagement.exception.ResourceNotFoundException;
import com.example.complaintmanagement.repository.*;
import com.example.complaintmanagement.service.ComplaintService;
import com.example.complaintmanagement.service.NotificationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ComplaintHistoryRepository historyRepository;
    private final DepartmentRepository departmentRepository;
    private final ComplaintAIService aiService;
    private final NotificationService notificationService;

    public ComplaintServiceImpl(
            ComplaintRepository complaintRepository,
            UserRepository userRepository,
            CommentRepository commentRepository,
            ComplaintHistoryRepository historyRepository,
            DepartmentRepository departmentRepository,
            ComplaintAIService aiService,
            NotificationService notificationService
    ) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.historyRepository = historyRepository;
        this.departmentRepository = departmentRepository;
        this.aiService = aiService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ComplaintResponse createComplaint(CreateComplaintRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // 1. Run AI analysis (with built-in fallback)
        AIResponse aiResult = aiService.analyzeComplaint(request.getTitle(), request.getDescription());

        // Map AI result to enums
        Category category = Category.OTHER;
        try {
            category = Category.valueOf(aiResult.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            // keep OTHER
        }

        Priority priority = Priority.MEDIUM;
        try {
            priority = Priority.valueOf(aiResult.getPriority().toUpperCase());
        } catch (IllegalArgumentException e) {
            // keep MEDIUM
        }

        // 2. Build and save the Complaint
        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .priority(priority)
                .status(Status.OPEN)
                .aiSummary(aiResult.getSummary())
                .aiSuggestedResponse(aiResult.getSuggestedResponse())
                .createdBy(user)
                .build();

        Complaint savedComplaint = complaintRepository.save(complaint);

        // 3. Log initial history
        ComplaintHistory history = ComplaintHistory.builder()
                .complaint(savedComplaint)
                .oldStatus(null)
                .newStatus(Status.OPEN)
                .changedBy(user)
                .build();
        historyRepository.save(history);

        // 4. Create Notification
        notificationService.createNotification(user, savedComplaint, 
                "Your complaint '" + request.getTitle() + "' has been raised. AI Category: " + category + ", Priority: " + priority);

        return mapToComplaintResponse(savedComplaint);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        // Security check
        if (user.getRole() == Role.USER && !complaint.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied: You do not own this complaint.");
        }
        if (user.getRole() == Role.TECHNICIAN && (complaint.getAssignedTo() == null || !complaint.getAssignedTo().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Access Denied: This complaint is not assigned to you.");
        }

        return mapToComplaintResponse(complaint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return complaintRepository.findByCreatedByIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToComplaintResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsForTechnician(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return complaintRepository.findByAssignedToIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToComplaintResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getAllComplaints(String sortBy, String direction) {
        List<Complaint> list = complaintRepository.findAllByOrderByCreatedAtDesc();

        // Demonstrate Sorting using Java Comparator on Collections
        Comparator<Complaint> comparator = Comparator.comparing(Complaint::getCreatedAt);

        if ("priority".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparingInt(c -> c.getPriority().getWeight());
        } else if ("updatedDate".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Complaint::getUpdatedAt);
        } else if ("status".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(c -> c.getStatus().name());
        }

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return list.stream()
                .sorted(comparator)
                .map(this::mapToComplaintResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getPrioritizedComplaints() {
        // Demonstrate DSA PriorityQueue
        // Higher Priority weight first, older createdDate first if weights are equal
        Comparator<Complaint> priorityComparator = (c1, c2) -> {
            int diff = Integer.compare(c2.getPriority().getWeight(), c1.getPriority().getWeight());
            if (diff != 0) {
                return diff;
            }
            return c1.getCreatedAt().compareTo(c2.getCreatedAt());
        };

        PriorityQueue<Complaint> pq = new PriorityQueue<>(priorityComparator);
        pq.addAll(complaintRepository.findAll());

        List<ComplaintResponse> sortedList = new ArrayList<>();
        while (!pq.isEmpty()) {
            sortedList.add(mapToComplaintResponse(pq.poll()));
        }
        return sortedList;
    }

    @Override
    @Transactional
    public ComplaintResponse assignComplaint(Long id, Long technicianId, String email) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found: " + technicianId));

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new BadRequestException("User selected is not a technician");
        }

        Status oldStatus = complaint.getStatus();
        complaint.setAssignedTo(technician);
        complaint.setStatus(Status.ASSIGNED);
        Complaint saved = complaintRepository.save(complaint);

        // Record history
        ComplaintHistory history = ComplaintHistory.builder()
                .complaint(saved)
                .oldStatus(oldStatus)
                .newStatus(Status.ASSIGNED)
                .changedBy(admin)
                .build();
        historyRepository.save(history);

        // Notify both creator and technician
        notificationService.createNotification(complaint.getCreatedBy(), saved, 
                "Your complaint '" + complaint.getTitle() + "' has been assigned to " + technician.getName());
        notificationService.createNotification(technician, saved, 
                "A new complaint has been assigned to you: '" + complaint.getTitle() + "'");

        return mapToComplaintResponse(saved);
    }

    @Override
    @Transactional
    public ComplaintResponse updateComplaintPriority(Long id, Priority priority, String email) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        complaint.setPriority(priority);
        Complaint saved = complaintRepository.save(complaint);

        notificationService.createNotification(complaint.getCreatedBy(), saved, 
                "Priority of your complaint '" + complaint.getTitle() + "' was changed to " + priority);

        return mapToComplaintResponse(saved);
    }

    @Override
    @Transactional
    public ComplaintResponse updateComplaintCategory(Long id, Category category, String email) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));

        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        complaint.setCategory(category);
        Complaint saved = complaintRepository.save(complaint);

        notificationService.createNotification(complaint.getCreatedBy(), saved, 
                "Category of your complaint '" + complaint.getTitle() + "' was changed to " + category);

        return mapToComplaintResponse(saved);
    }

    @Override
    @Transactional
    public ComplaintResponse updateComplaintStatus(Long id, Status status, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));

        Status oldStatus = complaint.getStatus();

        // 1. Strict Transition Validation
        if (oldStatus == Status.CLOSED) {
            throw new BadRequestException("Complaint is already CLOSED and cannot be modified.");
        }

        if (user.getRole() == Role.USER) {
            // USER can only transition RESOLVED -> CLOSED
            if (!complaint.getCreatedBy().getId().equals(user.getId())) {
                throw new AccessDeniedException("Access Denied: You do not own this complaint.");
            }
            if (status != Status.CLOSED) {
                throw new BadRequestException("Users can only change complaint status to CLOSED after it is resolved.");
            }
            if (oldStatus != Status.RESOLVED) {
                throw new BadRequestException("Cannot close a complaint that is not marked as RESOLVED.");
            }
        } else if (user.getRole() == Role.TECHNICIAN) {
            // TECHNICIAN can transition ASSIGNED -> IN_PROGRESS -> RESOLVED
            if (complaint.getAssignedTo() == null || !complaint.getAssignedTo().getId().equals(user.getId())) {
                throw new AccessDeniedException("Access Denied: This complaint is not assigned to you.");
            }
            if (status != Status.IN_PROGRESS && status != Status.RESOLVED) {
                throw new BadRequestException("Technicians can only update status to IN_PROGRESS or RESOLVED.");
            }
            if (status == Status.IN_PROGRESS && oldStatus != Status.ASSIGNED) {
                throw new BadRequestException("Cannot change status to IN_PROGRESS from state: " + oldStatus);
            }
            if (status == Status.RESOLVED && oldStatus != Status.IN_PROGRESS) {
                throw new BadRequestException("Cannot resolve a complaint that is not IN_PROGRESS.");
            }
        }

        // Apply changes
        complaint.setStatus(status);
        if (status == Status.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }
        Complaint saved = complaintRepository.save(complaint);

        // Record history
        ComplaintHistory history = ComplaintHistory.builder()
                .complaint(saved)
                .oldStatus(oldStatus)
                .newStatus(status)
                .changedBy(user)
                .build();
        historyRepository.save(history);

        // Notify
        if (status == Status.RESOLVED) {
            notificationService.createNotification(complaint.getCreatedBy(), saved, 
                    "Your complaint '" + complaint.getTitle() + "' is marked as RESOLVED. Please review and close it.");
        } else if (status == Status.CLOSED) {
            if (complaint.getAssignedTo() != null) {
                notificationService.createNotification(complaint.getAssignedTo(), saved, 
                        "Complaint '" + complaint.getTitle() + "' has been closed by the user.");
            }
        } else {
            notificationService.createNotification(complaint.getCreatedBy(), saved, 
                    "Status of complaint '" + complaint.getTitle() + "' changed from " + oldStatus + " to " + status);
        }

        return mapToComplaintResponse(saved);
    }

    @Override
    @Transactional
    public CommentResponse addComment(Long id, CommentRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));

        // Access check
        if (user.getRole() == Role.USER && !complaint.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied: You cannot comment on this complaint.");
        }
        if (user.getRole() == Role.TECHNICIAN && (complaint.getAssignedTo() == null || !complaint.getAssignedTo().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Access Denied: This complaint is not assigned to you.");
        }

        Comment comment = Comment.builder()
                .complaint(complaint)
                .user(user)
                .comment(request.getComment())
                .build();

        Comment savedComment = commentRepository.save(comment);

        // Notify other party
        if (user.getId().equals(complaint.getCreatedBy().getId())) {
            if (complaint.getAssignedTo() != null) {
                notificationService.createNotification(complaint.getAssignedTo(), complaint, 
                        "New comment from creator on complaint: '" + complaint.getTitle() + "'");
            }
        } else {
            notificationService.createNotification(complaint.getCreatedBy(), complaint, 
                    "New comment on your complaint: '" + complaint.getTitle() + "' from " + user.getName());
        }

        return mapToCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsForComplaint(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));

        // Access check
        if (user.getRole() == Role.USER && !complaint.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied: You cannot view comments on this complaint.");
        }
        if (user.getRole() == Role.TECHNICIAN && (complaint.getAssignedTo() == null || !complaint.getAssignedTo().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Access Denied: This complaint is not assigned to you.");
        }

        return commentRepository.findByComplaintIdOrderByCreatedAtAsc(id).stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintHistory> getComplaintHistory(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));

        // Access check
        if (user.getRole() == Role.USER && !complaint.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied: You cannot view history of this complaint.");
        }
        if (user.getRole() == Role.TECHNICIAN && (complaint.getAssignedTo() == null || !complaint.getAssignedTo().getId().equals(user.getId()))) {
            throw new AccessDeniedException("Access Denied: This complaint is not assigned to you.");
        }

        return historyRepository.findByComplaintIdOrderByChangedAtAsc(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsResponse getDashboardStatistics() {
        List<Complaint> complaints = complaintRepository.findAll();

        long total = complaints.size();
        long pending = 0;
        long inProgress = 0;
        long resolved = 0;
        long critical = 0;

        // Demonstrate DSA HashMap for counting categories and priorities dynamically
        Map<String, Long> categoryCounts = new HashMap<>();
        Map<String, Long> priorityCounts = new HashMap<>();

        // Initialize maps
        for (Category cat : Category.values()) {
            categoryCounts.put(cat.name(), 0L);
        }
        for (Priority prio : Priority.values()) {
            priorityCounts.put(prio.name(), 0L);
        }

        long totalResolutionMinutes = 0;
        long resolvedCount = 0;

        for (Complaint c : complaints) {
            // Count Statuses
            if (c.getStatus() == Status.OPEN || c.getStatus() == Status.ASSIGNED) {
                pending++;
            } else if (c.getStatus() == Status.IN_PROGRESS) {
                inProgress++;
            } else if (c.getStatus() == Status.RESOLVED || c.getStatus() == Status.CLOSED) {
                resolved++;
            }

            if (c.getPriority() == Priority.CRITICAL) {
                critical++;
            }

            // Category hashmap count
            String catName = c.getCategory().name();
            categoryCounts.put(catName, categoryCounts.getOrDefault(catName, 0L) + 1);

            // Priority hashmap count
            String prioName = c.getPriority().name();
            priorityCounts.put(prioName, priorityCounts.getOrDefault(prioName, 0L) + 1);

            // Resolution Time Calculation
            if (c.getResolvedAt() != null) {
                Duration d = Duration.between(c.getCreatedAt(), c.getResolvedAt());
                totalResolutionMinutes += d.toMinutes();
                resolvedCount++;
            }
        }

        double averageHours = 0.0;
        if (resolvedCount > 0) {
            averageHours = (double) totalResolutionMinutes / (60.0 * resolvedCount);
            // Round to 2 decimal places
            averageHours = Math.round(averageHours * 100.0) / 100.0;
        }

        return DashboardStatisticsResponse.builder()
                .totalComplaints(total)
                .pendingComplaints(pending)
                .inProgressComplaints(inProgress)
                .resolvedComplaints(resolved)
                .criticalComplaints(critical)
                .complaintsByCategory(categoryCounts)
                .complaintsByPriority(priorityCounts)
                .averageResolutionTimeInHours(averageHours)
                .build();
    }

    // Helper mappers
    private ComplaintResponse mapToComplaintResponse(Complaint c) {
        return ComplaintResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .priority(c.getPriority())
                .status(c.getStatus())
                .aiSummary(c.getAiSummary())
                .aiSuggestedResponse(c.getAiSuggestedResponse())
                .createdBy(mapToUserResponse(c.getCreatedBy()))
                .assignedTo(c.getAssignedTo() != null ? mapToUserResponse(c.getAssignedTo()) : null)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .resolvedAt(c.getResolvedAt())
                .build();
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

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .complaintId(comment.getComplaint().getId())
                .user(mapToUserResponse(comment.getUser()))
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
