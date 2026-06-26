package com.project.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for capturing incoming Employee creation or update payloads.
 *
 * DESIGN HIGHLIGHTS:
 * - Uses a Java 'record' to act as an immutable data holder for client requests.
 * - Leverages Jakarta Validation constraints to automatically sanitize and validate incoming input.
 * - Excludes database-managed fields like 'id', 'createdAt', or 'updatedAt' since clients shouldn't provide them.
 */
public record EmployeeRequest (

        @NotBlank(message = "Employee code is required") // Cannot be null, empty, or just whitespace
        String employeeCode,

        @NotBlank(message = "First Name is required")
        String firstName,

        @NotBlank(message = "Last Name is required")
        String lastName,

        @Email(message = "Invalid email format")        // Enforces a structured format (e.g., user@domain.com)
        @NotBlank(message = "Email is required")
        String email,

        String phone,       // Optional field (no validation constraints applied)

        String departmemnt, // Optional field (Tip: Watch out for the minor typo 'departmemnt' here!)

        String designation, // Optional field

        @NotNull(message = "Salary is required")
        @Positive(message = "Salary must be greater than zero") // Enforces that compensation figures must be a positive number
        BigDecimal salary,

        @NotNull(message = "Joining date is required") // Ensures a valid calendar date is provided
        LocalDate joiningDate
) {}