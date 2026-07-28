package com.project.ems.security.service.impl;

import com.project.ems.exception.DuplicateResourceException;
import com.project.ems.security.dto.AuthenticationResponse;
import com.project.ems.security.dto.LoginRequest;
import com.project.ems.security.dto.RegisterRequest;
import com.project.ems.security.entity.Role;
import com.project.ems.security.entity.RoleType;
import com.project.ems.security.entity.User;
import com.project.ems.security.exception.InvalidCredentialsException;
import com.project.ems.security.jwt.JwtService;
import com.project.ems.security.repository.RoleRepository;
import com.project.ems.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthenticationServiceImpl.
 *
 * These tests verify registration and login business logic
 * without starting Spring Boot or connecting to MySQL.
 *
 * Repositories, PasswordEncoder, AuthenticationManager and
 * JwtService are replaced with Mockito mocks.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    /**
     * Mock repository used for User database operations.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mock repository used for Role database operations.
     */
    @Mock
    private RoleRepository roleRepository;

    /**
     * Mock password encoder.
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * Mock Spring Security authentication manager.
     */
    @Mock
    private AuthenticationManager authenticationManager;

    /**
     * Mock JWT service.
     */
    @Mock
    private JwtService jwtService;

    /**
     * Mock Authentication object returned after successful login.
     */
    @Mock
    private Authentication authentication;

    /**
     * Real AuthenticationServiceImpl under test.
     *
     * Mockito automatically injects all mocked dependencies.
     */
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    /**
     * Verifies that a new user is registered successfully.
     */
    @Test
    void register_shouldRegisterUserSuccessfully() {

        // Arrange: prepare the registration request.
        RegisterRequest request = createRegisterRequest();

        Role adminRole = createAdminRole();

        // Simulate that username and email are available.
        when(userRepository.existsByUsername("admin"))
                .thenReturn(false);

        when(userRepository.existsByEmail("admin@example.com"))
                .thenReturn(false);

        // Simulate finding the ADMIN role.
        when(roleRepository.findByName(RoleType.ADMIN))
                .thenReturn(Optional.of(adminRole));

        // Simulate BCrypt password encoding.
        when(passwordEncoder.encode("Password@123"))
                .thenReturn("$2a$10$encodedPassword");

        /*
         * Simulate saving the User entity.
         *
         * The database normally generates the ID,
         * so the mock assigns an ID before returning it.
         */
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {

                    User user = invocation.getArgument(0);
                    user.setId(1L);

                    return user;
                });

        // Simulate JWT generation.
        when(jwtService.generateToken("admin", "ADMIN"))
                .thenReturn("generated-jwt-token");

        // Act
        AuthenticationResponse response =
                authenticationService.register(request);

        // Assert the returned response.
        assertNotNull(response);
        assertEquals("generated-jwt-token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals("admin", response.username());
        assertEquals("ADMIN", response.role());

        /*
         * Capture the User passed to repository.save()
         * so we can verify the stored password and role.
         */
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("admin", savedUser.getUsername());
        assertEquals("admin@example.com", savedUser.getEmail());

        // Password must be encoded, not stored as plain text.
        assertEquals(
                "$2a$10$encodedPassword",
                savedUser.getPassword()
        );

        assertEquals(RoleType.ADMIN, savedUser.getRole().getName());
        assertTrue(savedUser.getEnabled());
        assertTrue(savedUser.getAccountNonLocked());

        // Verify important dependency calls.
        verify(passwordEncoder, times(1))
                .encode("Password@123");

        verify(jwtService, times(1))
                .generateToken("admin", "ADMIN");
    }

    /**
     * Verifies that registration fails when the username already exists.
     */
    @Test
    void register_shouldThrowExceptionWhenUsernameExists() {

        // Arrange
        RegisterRequest request = createRegisterRequest();

        when(userRepository.existsByUsername("admin"))
                .thenReturn(true);

        // Act and Assert
        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> authenticationService.register(request)
                );

        assertEquals(
                "Username already exists: admin",
                exception.getMessage()
        );

        /*
         * Processing must stop immediately.
         *
         * The password must not be encoded and the user
         * must not be saved.
         */
        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(jwtService, never())
                .generateToken(anyString(), anyString());
    }

    /**
     * Verifies successful login using valid username and password.
     */
    @Test
    void login_shouldAuthenticateUserSuccessfully() {

        // Arrange
        LoginRequest request = new LoginRequest(
                "admin",
                "Password@123"
        );

        User user = createAdminUser();

        /*
         * Simulate successful authentication.
         *
         * AuthenticationManager normally calls UserDetailsService
         * and PasswordEncoder internally.
         */
        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        // Simulate loading the complete User entity after authentication.
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        /*
         * The login service updates lastLogin and saves the user.
         */
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Simulate token generation.
        when(jwtService.generateToken("admin", "ADMIN"))
                .thenReturn("login-jwt-token");

        // Act
        AuthenticationResponse response =
                authenticationService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("login-jwt-token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals("admin", response.username());
        assertEquals("ADMIN", response.role());

        // Verify that lastLogin was updated.
        assertNotNull(user.getLastLogin());

        verify(authenticationManager, times(1))
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(userRepository, times(1))
                .findByUsername("admin");

        verify(jwtService, times(1))
                .generateToken("admin", "ADMIN");
    }

    /**
     * Verifies that invalid credentials produce a safe
     * InvalidCredentialsException.
     */
    @Test
    void login_shouldThrowExceptionWhenCredentialsAreInvalid() {

        // Arrange
        LoginRequest request = new LoginRequest(
                "admin",
                "WrongPassword"
        );

        /*
         * Simulate Spring Security rejecting the credentials.
         */
        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new BadCredentialsException("Bad credentials")
        );

        // Act and Assert
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authenticationService.login(request)
                );

        /*
         * The response deliberately does not reveal whether
         * the username or password was incorrect.
         */
        assertEquals(
                "Invalid username or password.",
                exception.getMessage()
        );

        /*
         * The service must not load the User again or generate
         * a token after authentication failure.
         */
        verify(userRepository, never())
                .findByUsername(anyString());

        verify(jwtService, never())
                .generateToken(anyString(), anyString());
    }

    /**
     * Creates reusable registration input.
     */
    private RegisterRequest createRegisterRequest() {

        return new RegisterRequest(
                "admin",
                "admin@example.com",
                "Password@123",
                RoleType.ADMIN
        );
    }

    /**
     * Creates an ADMIN role for tests.
     */
    private Role createAdminRole() {

        return Role.builder()
                .id(1L)
                .name(RoleType.ADMIN)
                .description("System Administrator")
                .build();
    }

    /**
     * Creates an existing ADMIN user for login tests.
     */
    private User createAdminUser() {

        return User.builder()
                .id(1L)
                .username("admin")
                .email("admin@example.com")
                .password("$2a$10$encodedPassword")
                .enabled(true)
                .accountNonLocked(true)
                .role(createAdminRole())
                .build();
    }
}