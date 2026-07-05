package com.project.ems.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response returned by the application
 * whenever an exception occurs.
 *
 * This class provides detailed information about the error
 * to help API consumers understand what went wrong.
 */
@Builder
public record ErrorResponse(

        // Time when the error occurred
        LocalDateTime timestamp,

        // HTTP status code (404, 400, 500, etc.)
        int status,

        // HTTP status reason phrase (Not Found, Bad Request, etc.)
        String error,

        // Human-readable error message
        String message,

        // Requested API path
        String path,

        // Validation errors (only used for validation failures)
        Map<String, String> validationErrors

) {}