package com.project.ems.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a clean Employee response payload.
 *
 * DESIGN HIGHLIGHTS:
 * - Uses a Java 'record' to enforce data immutability (final fields, no setters).
 * - Automatically provides constructor, equals(), hashCode(), and toString() behind the scenes.
 * - Used exclusively for sending safe, filtered employee data back to the client/frontend API.
 * - Excludes internal database auditing fields (like createdAt/updatedAt) to optimize payload size.
 */
public record EmployeeResponse (
        // Unique database identifier for the employee record
        Long id,

        // Public business identifier (e.g., "EMP-1002")
        String employeeCode,

        String firstName,
        String lastName,
        String email,
        String phone,
        String department,
        String designation,

        // Exact decimal precision for salary to prevent floating-point calculation errors
        BigDecimal salary,

        // Stored as LocalDate here for standard ISO date formatting (YYYY-MM-DD) in API JSON responses
        LocalDate joiningDate,

        // Represents active/inactive status of the employee
        Boolean status
){}