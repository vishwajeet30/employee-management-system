package com.project.ems.security.service.impl;

import com.project.ems.exception.DuplicateResourceException;
import com.project.ems.exception.ResourceNotFoundException;
import com.project.ems.security.dto.AuthenticationResponse;
import com.project.ems.security.dto.LoginRequest;
import com.project.ems.security.dto.RegisterRequest;
import com.project.ems.security.entity.Role;
import com.project.ems.security.entity.RoleType;
import com.project.ems.security.entity.User;
import com.project.ems.security.exception.InvalidCredentialsException;
import com.project.ems.security.jwt.JwtService;
import com.project.ems.security.mapper.UserMapper;
import com.project.ems.security.repository.RoleRepository;
import com.project.ems.security.repository.UserRepository;
import com.project.ems.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementation responsible for user registration
 * and username/password authentication.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * Repository used for User database operations.
     */
    private final UserRepository userRepository;

    /**
     * Repository used for Role database operations.
     */
    private final RoleRepository roleRepository;

    /**
     * Encoder used to securely hash passwords during registration.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Spring Security component used to verify login credentials.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Service used to generate signed JWT access tokens.
     */
    private final JwtService jwtService;

    /**
     * Registers a new application user.
     *
     * Registration flow:
     * 1. Check whether username already exists.
     * 2. Check whether email already exists.
     * 3. Resolve the requested role.
     * 4. Encode the password using BCrypt.
     * 5. Save the user.
     * 6. Generate a JWT token.
     *
     * @param request registration details
     * @return authentication response containing JWT
     */
    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

        // Prevent duplicate usernames
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException(
                    "Username already exists: " + request.username()
            );
        }

        // Prevent duplicate email addresses
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Email already exists: " + request.email()
            );
        }

        /*
         * Assign EMPLOYEE when no role is provided.
         *
         * Later, public registration should always assign EMPLOYEE.
         * ADMIN and HR creation should be restricted to an administrator.
         */
        RoleType requestedRole = request.role() == null
                ? RoleType.EMPLOYEE
                : request.role();

        // Fetch the corresponding Role entity from the database
        Role role = roleRepository.findByName(requestedRole)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role not found: " + requestedRole
                ));

        // Convert the plain-text password into a BCrypt hash
        String encodedPassword =
                passwordEncoder.encode(request.password());

        // Convert request data into the User entity
        User user = UserMapper.toEntity(
                request,
                encodedPassword,
                role
        );

        // Persist the new user
        User savedUser = userRepository.save(user);

        // Generate a signed access token for the registered user
        String accessToken = jwtService.generateToken(
                savedUser.getUsername(),
                savedUser.getRole().getName().name()
        );

        // Return user information and JWT token
        return UserMapper.toAuthenticationResponse(
                savedUser,
                accessToken
        );
    }

    /**
     * Authenticates a user using username and password.
     *
     * Login flow:
     * 1. Pass credentials to AuthenticationManager.
     * 2. Spring Security loads the user through UserDetailsService.
     * 3. Spring Security compares the submitted password with the hash.
     * 4. Load the complete User entity.
     * 5. Update the last-login timestamp.
     * 6. Generate and return a JWT token.
     *
     * @param request username and password
     * @return authentication response containing JWT
     */
    @Override
    @Transactional
    public AuthenticationResponse login(LoginRequest request) {

        try {
            /*
             * AuthenticationManager uses the configured UserDetailsService
             * and PasswordEncoder to verify the credentials.
             */
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

        } catch (AuthenticationException exception) {

            /*
             * Do not reveal whether the username or password was wrong.
             * A generic message is safer.
             */
            throw new InvalidCredentialsException(
                    "Invalid username or password."
            );
        }

        /*
         * Authentication succeeded, so retrieve the application's
         * complete User entity.
         */
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid username or password."
                ));

        // Record the successful login time
        user.setLastLogin(LocalDateTime.now());

        /*
         * Because this method is transactional and the entity is managed,
         * JPA can persist this update automatically at transaction commit.
         * Calling save explicitly also makes the intention clear.
         */
        User savedUser = userRepository.save(user);

        // Generate JWT token for subsequent secured API calls
        String accessToken = jwtService.generateToken(
                savedUser.getUsername(),
                savedUser.getRole().getName().name()
        );

        // Build and return the login response
        return UserMapper.toAuthenticationResponse(
                savedUser,
                accessToken
        );
    }
}