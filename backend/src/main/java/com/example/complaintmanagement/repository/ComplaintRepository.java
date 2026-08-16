package com.example.complaintmanagement.repository;

import com.example.complaintmanagement.entity.Complaint;
import com.example.complaintmanagement.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByCreatedByIdOrderByCreatedAtDesc(Long userId);
    List<Complaint> findByAssignedToIdOrderByCreatedAtDesc(Long technicianId);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    long countByStatus(Status status);
}
