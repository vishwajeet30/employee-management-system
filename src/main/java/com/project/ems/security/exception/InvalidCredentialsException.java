package com.project.ems.security.exception;

/**
 * Exception thrown when a login attempt contains
 * an invalid username or password.
 *
 * This exception will be converted into an HTTP 401 response
 * by the GlobalExceptionHandler.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Creates an InvalidCredentialsException with a custom message.
     *
     * @param message error message
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}