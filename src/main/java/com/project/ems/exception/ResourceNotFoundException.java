package com.project.ems.exception;

/**
 * Custom Business Exception thrown whenever a requested database record is not found.
 *
 * DESIGN HIGHLIGHTS:
 * - Extends 'RuntimeException' so it behaves as an unchecked exception.
 * - This means you don't have to clutter your service layer methods with 'throws' declarations.
 * - Commonly intercepted by a Global Exception Handler (@ControllerAdvice) to automatically
 *   return an HTTP 404 Status Code (Not Found) directly to the client API.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with a custom dynamic error message.
     * Pass details like: "Employee not found with ID: " + id
     *
     * @param message The detailed message text explaining the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message); // Passes the error message up to the parent RuntimeException class
    }
}

/*
* super(message);
Calling the Parent: super refers to the parent class, which is RuntimeException (as defined in class ResourceNotFoundException extends RuntimeException).
The Action: It passes the message string up to the RuntimeException constructor.
Why is this necessary?
The RuntimeException class (and its ancestors Exception and Throwable) has a built-in field to store an error message.
By calling super(message), you are telling the parent class: "Store this specific text so that when someone prints the stack trace or calls .getMessage(), they see this text."

**/