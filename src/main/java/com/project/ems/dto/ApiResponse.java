package com.project.ems.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Standard response wrapper for successful API responses.
 */
@Builder
@Schema(
        name = "API Response",
        description = "Standard wrapper returned for successful API responses."
)
public record ApiResponse<T>(

        @Schema(description = "Indicates whether the request was successful", example = "true")
        boolean success,

        @Schema(description = "Success message", example = "Employee created successfully.")
        String message,

        @Schema(description = "Actual response data")
        T data,

        @Schema(description = "Time at which the response was generated")
        LocalDateTime timestamp

) {}