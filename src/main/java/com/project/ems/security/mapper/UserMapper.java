package com.project.ems.security.mapper;

import com.project.ems.security.dto.AuthenticationResponse;
import com.project.ems.security.dto.RegisterRequest;
import com.project.ems.security.entity.Role;
import com.project.ems.security.entity.User;

/**
 * Utility class for converting User-related DTOs and entities.
 */
public final class UserMapper {

    private UserMapper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts RegisterRequest into User entity.
     */
    public static User toEntity(
            RegisterRequest request,
            String encodedPassword,
            Role role) {

        return User.builder()
                .username(request.username())
                .email(request.email())
                .password(encodedPassword)
                .role(role)
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }

    /**
     * Converts User entity into AuthenticationResponse.
     */
    public static AuthenticationResponse toAuthenticationResponse(
            User user,
            String accessToken) {

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole().getName().name())
                .build();
    }
}