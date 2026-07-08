package com.project.ems.security.service;

import com.project.ems.exception.DuplicateResourceException;
import com.project.ems.exception.ResourceNotFoundException;
import com.project.ems.security.dto.AuthenticationResponse;
import com.project.ems.security.dto.LoginRequest;
import com.project.ems.security.dto.RegisterRequest;
import com.project.ems.security.entity.Role;
import com.project.ems.security.entity.RoleType;
import com.project.ems.security.entity.User;
import com.project.ems.security.jwt.JwtService;
import com.project.ems.security.mapper.UserMapper;
import com.project.ems.security.repository.RoleRepository;
import com.project.ems.security.repository.UserRepository;
import com.project.ems.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation class for authentication-related business logic.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Registers a new user.
     *
     * Steps:
     * 1. Check duplicate username
     * 2. Check duplicate email
     * 3. Fetch role from database
     * 4. Encrypt password
     * 5. Save user
     * 6. Generate JWT token
     */
    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

        // Check if username is already used
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException(
                    "Username already exists: " + request.username());
        }

        // Check if email is already used
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Email already exists: " + request.email());
        }

        // If role is null, assign EMPLOYEE as default role
        RoleType requestedRole = request.role() == null
                ? RoleType.EMPLOYEE
                : request.role();

        // Fetch role from roles table
        Role role = roleRepository.findByName(requestedRole)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found: " + requestedRole));

        // Encrypt raw password using BCrypt
        String encodedPassword =
                passwordEncoder.encode(request.password());

        // Convert request DTO into User entity
        User user = UserMapper.toEntity(
                request,
                encodedPassword,
                role
        );

        // Save user in database
        User savedUser = userRepository.save(user);

        // Generate JWT token after successful registration
        String token = jwtService.generateToken(
                savedUser.getUsername(),
                savedUser.getRole().getName().name()
        );

        // Return authentication response with token
        return UserMapper.toAuthenticationResponse(savedUser, token);
    }

    /**
     * Login will be implemented in the next step.
     */
    @Override
    public AuthenticationResponse login(LoginRequest request) {
        return null;
    }
}