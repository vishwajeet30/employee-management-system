package com.project.ems.security.controller;

import com.project.ems.dto.ApiResponse;
import com.project.ems.security.dto.AuthenticationResponse;
import com.project.ems.security.dto.RegisterRequest;
import com.project.ems.security.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller for authentication APIs.
 *
 * This controller handles:
 * - User registration
 * - User login later
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    /**
     * Service containing authentication business logic.
     */
    private final AuthenticationService authenticationService;

    /**
     * Registers a new user and returns JWT token.
     *
     * Endpoint:
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        // Delegate registration logic to service layer
        AuthenticationResponse authResponse =
                authenticationService.register(request);

        // Wrap response in common API response format
        ApiResponse<AuthenticationResponse> response =
                ApiResponse.<AuthenticationResponse>builder()
                        .success(true)
                        .message("User registered successfully.")
                        .data(authResponse)
                        .timestamp(LocalDateTime.now())
                        .build();

        // Return HTTP 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}