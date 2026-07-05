package com.project.ems.exception;

import com.project.ems.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the entire application.
 *
 * Any exception thrown from a Controller will be handled here,
 * resulting in consistent and user-friendly API responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where a requested resource (Employee, Department, etc.)
     * does not exist.
     *
     * Returns HTTP Status: 404 NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles duplicate resource exceptions.
     *
     * Example:
     * - Duplicate email
     * - Duplicate employee code
     *
     * Returns HTTP Status: 409 CONFLICT
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResourceException(
            DuplicateResourceException ex) {

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handles Bean Validation failures.
     *
     * Example:
     * - Blank first name
     * - Invalid email
     * - Negative salary
     *
     * Returns HTTP Status: 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // Collect validation errors into a map
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> response =
                ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed.")
                        .data(errors)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles all unexpected exceptions.
     *
     * This acts as a safety net for any exception that
     * isn't handled by another @ExceptionHandler method.
     *
     * Returns HTTP Status: 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex) {

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message("An unexpected error occurred.")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        // In production, log the exception here using a logger.
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}