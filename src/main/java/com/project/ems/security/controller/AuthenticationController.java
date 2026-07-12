package com.project.ems.security.controller;

import com.project.ems.dto.ApiResponse;
import com.project.ems.security.dto.AuthenticationResponse;
import com.project.ems.security.dto.LoginRequest;
import com.project.ems.security.dto.RegisterRequest;
import com.project.ems.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller responsible for authentication endpoints.
 *
 * These endpoints are public and do not require an existing JWT.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "APIs for user registration and login"
)
public class AuthenticationController {

    /**
     * Service containing authentication business logic.
     */
    private final AuthenticationService authenticationService;

    /**
     * Registers a new user and returns a JWT access token.
     *
     * Endpoint:
     * POST /api/v1/auth/register
     *
     * @param request validated registration request
     * @return registered-user information and JWT token
     */
    @Operation(
            summary = "Register user",
            description = "Creates a new user account and returns a JWT access token."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthenticationResponse authenticationResponse =
                authenticationService.register(request);

        ApiResponse<AuthenticationResponse> response =
                ApiResponse.<AuthenticationResponse>builder()
                        .success(true)
                        .message("User registered successfully.")
                        .data(authenticationResponse)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Authenticates an existing user and returns a JWT access token.
     *
     * Endpoint:
     * POST /api/v1/auth/login
     *
     * @param request validated login credentials
     * @return authenticated-user information and JWT token
     */
    @Operation(
            summary = "Login user",
            description = "Authenticates a username and password and returns a JWT access token."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthenticationResponse authenticationResponse =
                authenticationService.login(request);

        ApiResponse<AuthenticationResponse> response =
                ApiResponse.<AuthenticationResponse>builder()
                        .success(true)
                        .message("Login successful.")
                        .data(authenticationResponse)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }
}