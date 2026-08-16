package com.example.complaintmanagement.repository;

import com.example.complaintmanagement.entity.ComplaintHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintHistoryRepository extends JpaRepository<ComplaintHistory, Long> {
    List<ComplaintHistory> findByComplaintIdOrderByChangedAtAsc(Long complaintId);
}
