package com.project.ems.security.service;

import com.project.ems.security.dto.AuthenticationResponse;
import com.project.ems.security.dto.LoginRequest;
import com.project.ems.security.dto.RegisterRequest;

/**
 * Service interface responsible for handling
 * authentication-related business operations.
 */
public interface AuthenticationService {

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request containing user details
     * @return authentication response after successful registration
     */
    AuthenticationResponse register(RegisterRequest request);

    /**
     * Authenticates an existing user.
     *
     * NOTE:
     * JWT generation will be implemented later.
     *
     * @param request login request
     * @return authentication response
     */
    AuthenticationResponse login(LoginRequest request);
}