package com.project.ems.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard response returned whenever an exception occurs.
 */
@Builder
@Schema(
        name = "Error Response",
        description = "Standard error response returned by the application."
)
public record ErrorResponse(

        @Schema(description = "Timestamp of the error")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "HTTP error", example = "Not Found")
        String error,

        @Schema(description = "Detailed error message")
        String message,

        @Schema(description = "API path that caused the error")
        String path,

        @Schema(description = "Validation errors (if applicable)")
        Map<String, String> validationErrors

) {}