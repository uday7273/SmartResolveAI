package com.example.complaintmanagement.service.impl;

import com.example.complaintmanagement.dto.DepartmentRequest;
import com.example.complaintmanagement.entity.Department;
import com.example.complaintmanagement.exception.DuplicateResourceException;
import com.example.complaintmanagement.exception.ResourceNotFoundException;
import com.example.complaintmanagement.repository.DepartmentRepository;
import com.example.complaintmanagement.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    @Override
    @Transactional
    public Department createDepartment(DepartmentRequest request) {
        if (departmentRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Department with name '" + request.getName() + "' already exists");
        }

        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public Department updateDepartment(Long id, DepartmentRequest request) {
        Department department = getDepartmentById(id);

        departmentRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("Department with name '" + request.getName() + "' already exists");
            }
        });

        department.setName(request.getName());
        department.setDescription(request.getDescription());

        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }
}
