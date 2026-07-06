package com.project.ems.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Response returned after successful authentication.
 */
@Builder
@Schema(
        name = "Authentication Response",
        description = "Response returned after successful authentication."
)
public record AuthenticationResponse(

        @Schema(
                description = "JWT access token"
        )
        String accessToken,

        @Schema(
                description = "Authentication type",
                example = "Bearer"
        )
        String tokenType,

        @Schema(
                description = "Logged-in username",
                example = "vishwajeet"
        )
        String username,

        @Schema(
                description = "Logged-in user's role",
                example = "ADMIN"
        )
        String role

) {
}