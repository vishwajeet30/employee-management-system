package com.project.ems.dto;

import lombok.Builder;
import java.time.LocalDateTime;

/**
 * A generic wrapper class used to standardize API responses across the application.
 * Using a Java Record ensures this object is immutable and concise.
 *
 * @param <T> The type of the payload data returned in the response.
 */
@Builder
public record ApiResponse<T>(
        // Indicates if the operation was successful (true) or failed (false)
        boolean success,

        // A descriptive message providing context about the response (e.g., "User created successfully")
        String message,

        // The actual payload/content returned by the API endpoint
        // Note: You might want to rename this from 'date' to 'data' to avoid confusion with time/dates!
        T data,

        // The exact date and time when the response was generated
        LocalDateTime timestamp
) {}