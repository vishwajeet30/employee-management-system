package com.project.ems.exception;

/**
 * Exception thrown when the client sends a logically invalid request.
 *
 * Examples:
 * - Leave end date is before start date
 * - An already reviewed leave is reviewed again
 */
public class BadRequestException extends RuntimeException {

    /**
     * Creates an exception with a readable error message.
     *
     * @param message explanation of the invalid request
     */
    public BadRequestException(String message) {
        super(message);
    }
}