package com.example.complaintmanagement.controller;

import com.example.complaintmanagement.dto.LoginRequest;
import com.example.complaintmanagement.dto.LoginResponse;
import com.example.complaintmanagement.dto.RegisterRequest;
import com.example.complaintmanagement.dto.UserResponse;
import com.example.complaintmanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and authentication")
public class AuthenticationController {

    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user profile in the system.")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Verifies user credentials and issues a JWT Bearer token.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current logged in user details", description = "Returns profile details of the authenticated user.")
    public ResponseEntity<UserResponse> getMe(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.getMe(principal.getName()));
    }
}
