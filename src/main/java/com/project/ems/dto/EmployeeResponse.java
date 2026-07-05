package com.project.ems.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO returned to the client after successful employee operations.
 */
@Schema(
        name = "Employee Response",
        description = "Response object representing employee details."
)
public record EmployeeResponse(

        @Schema(description = "Database ID", example = "1")
        Long id,

        @Schema(description = "Employee code", example = "EMP001")
        String employeeCode,

        @Schema(description = "First name", example = "Vishwajeet")
        String firstName,

        @Schema(description = "Last name", example = "Singh")
        String lastName,

        @Schema(description = "Official email", example = "vishwajeet@example.com")
        String email,

        @Schema(description = "Phone number", example = "9876543210")
        String phone,

        @Schema(description = "Department", example = "IT")
        String department,

        @Schema(description = "Designation", example = "Software Engineer")
        String designation,

        @Schema(description = "Monthly salary", example = "65000")
        BigDecimal salary,

        @Schema(description = "Joining date", example = "2026-07-05")
        LocalDate joiningDate,

        @Schema(description = "Employee status", example = "true")
        Boolean status
) {}