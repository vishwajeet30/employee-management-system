package com.project.ems.exception;

/**
 * Custom Business Exception thrown when an operation attempts to create a duplicate record.
 * * DESIGN HIGHLIGHTS:
 * - Extends 'RuntimeException' to remain an unchecked exception, keeping method signatures clean.
 * - Triggered when unique constraints are about to be violated (e.g., duplicate email or employee code).
 * - Commonly caught by a Global Exception Handler (@ControllerAdvice) to automatically
 * return an HTTP 409 Status Code (Conflict) back to the client API.
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a new exception with a custom dynamic error message.
     * Pass details like: "Employee already exists with email: " + email
     * * @param message The detailed message text explaining what data caused the conflict
     */
    public DuplicateResourceException(String message) {
        super(message); // Passes the error message up to the parent RuntimeException class
    }
}