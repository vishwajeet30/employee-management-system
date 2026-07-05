package com.project.ems.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO used to register a new user.
 */
@Schema(
        name = "Register Request",
        description = "Request object used for user registration."
)
public record RegisterRequest(

        @Schema(
                description = "Unique username",
                example = "vishwajeet"
        )
        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 50,
                message = "Username must be between 4 and 50 characters")
        String username,

        @Schema(
                description = "User email",
                example = "vishwajeet@gmail.com"
        )
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @Schema(
                description = "Password",
                example = "Password@123"
        )
        @NotBlank(message = "Password is required")
        @Size(min = 8,
                message = "Password must contain at least 8 characters")
        String password,

        @Schema(
                description = "Role assigned to the user",
                example = "EMPLOYEE"
        )
        @NotBlank(message = "Role is required")
        String role
) {
}