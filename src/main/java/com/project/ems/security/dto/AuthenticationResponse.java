package com.project.ems.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Response returned after successful authentication.
 */
@Builder
@Schema(
        name = "Authentication Response",
        description = "JWT authentication response."
)
public record AuthenticationResponse(

        @Schema(
                description = "JWT Access Token",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        String accessToken,

        @Schema(
                description = "Token type",
                example = "Bearer"
        )
        String tokenType,

        @Schema(
                description = "Logged in username",
                example = "vishwajeet"
        )
        String username,

        @Schema(
                description = "User role",
                example = "EMPLOYEE"
        )
        String role

) {
}