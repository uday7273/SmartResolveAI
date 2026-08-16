package com.example.complaintmanagement.service;

import com.example.complaintmanagement.dto.*;
import com.example.complaintmanagement.entity.ComplaintHistory;
import com.example.complaintmanagement.enums.Category;
import com.example.complaintmanagement.enums.Priority;
import com.example.complaintmanagement.enums.Status;
import java.util.List;

public interface ComplaintService {
    ComplaintResponse createComplaint(CreateComplaintRequest request, String email);
    ComplaintResponse getComplaintById(Long id, String email);
    List<ComplaintResponse> getComplaintsForUser(String email);
    List<ComplaintResponse> getComplaintsForTechnician(String email);
    
    // Admin features
    List<ComplaintResponse> getAllComplaints(String sortBy, String direction);
    List<ComplaintResponse> getPrioritizedComplaints(); // DSA PriorityQueue ordering
    ComplaintResponse assignComplaint(Long id, Long technicianId, String email);
    ComplaintResponse updateComplaintPriority(Long id, Priority priority, String email);
    ComplaintResponse updateComplaintCategory(Long id, Category category, String email);
    
    // Status update (Role specific validation)
    ComplaintResponse updateComplaintStatus(Long id, Status status, String email);
    
    // Comments
    CommentResponse addComment(Long id, CommentRequest request, String email);
    List<CommentResponse> getCommentsForComplaint(Long id, String email);
    
    // Statistics & History
    DashboardStatisticsResponse getDashboardStatistics(); // DSA HashMap counts
    List<ComplaintHistory> getComplaintHistory(Long id, String email);
}
