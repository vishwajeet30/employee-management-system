package com.project.ems.exception;

import com.project.ems.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Every exception thrown from any REST controller is handled here,
 * ensuring consistent and meaningful error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Logger used to record application errors.
     * We'll configure Logback later in the project.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles Resource Not Found exceptions.
     *
     * Example:
     * Employee not found.
     * Department not found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        logger.error("Resource not found: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    /**
     * Handles duplicate resource exceptions.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        logger.error("Duplicate resource: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    /**
     * Handles Bean Validation exceptions.
     *
     * Triggered automatically whenever @Valid fails.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> validationErrors = new HashMap<>();

        // Collect every validation error
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()));

        logger.error("Validation failed.");

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed.",
                request.getRequestURI(),
                validationErrors
        );
    }

    /**
     * Handles every unexpected exception.
     *
     * This should always be the last exception handler.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Unexpected exception occurred.", ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please contact the administrator.",
                request.getRequestURI(),
                null
        );
    }

    /**
     * Utility method for creating a standardized ErrorResponse.
     *
     * This prevents duplicate builder code in every exception handler.
     *
     * @param status HTTP status code
     * @param message Error message
     * @param path Requested API endpoint
     * @param validationErrors Validation error map (if any)
     * @return ResponseEntity containing ErrorResponse
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> validationErrors) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}