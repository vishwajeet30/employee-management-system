package com.project.ems.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing the login request.
 */
@Schema(
        name = "Login Request",
        description = "Request payload used for user authentication."
)
public record LoginRequest(

        @Schema(
                description = "Username",
                example = "vishwajeet"
        )
        @NotBlank(message = "Username is required")
        String username,

        @Schema(
                description = "Password",
                example = "Password@123"
        )
        @NotBlank(message = "Password is required")
        String password

) {
}