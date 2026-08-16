package com.example.complaintmanagement.service;

import com.example.complaintmanagement.dto.DepartmentRequest;
import com.example.complaintmanagement.entity.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();
    Department getDepartmentById(Long id);
    Department createDepartment(DepartmentRequest request);
    Department updateDepartment(Long id, DepartmentRequest request);
    void deleteDepartment(Long id);
}
