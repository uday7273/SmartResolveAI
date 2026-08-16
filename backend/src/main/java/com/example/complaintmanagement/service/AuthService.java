package com.example.complaintmanagement.service;

import com.example.complaintmanagement.dto.LoginRequest;
import com.example.complaintmanagement.dto.LoginResponse;
import com.example.complaintmanagement.dto.RegisterRequest;
import com.example.complaintmanagement.dto.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    UserResponse getMe(String email);
}
