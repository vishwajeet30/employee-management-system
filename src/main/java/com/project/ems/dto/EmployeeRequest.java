package com.project.ems.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO used for creating or updating an employee.
 * This object represents the request body sent by the client.
 */
@Schema(
        name = "Employee Request",
        description = "Request object used to create or update an employee."
)
public record EmployeeRequest(

        @Schema(
                description = "Unique employee code",
                example = "EMP001"
        )
        @NotBlank(message = "Employee code is required")
        String employeeCode,

        @Schema(
                description = "Employee first name",
                example = "Vishwajeet"
        )
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(
                description = "Employee last name",
                example = "Singh"
        )
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(
                description = "Official email address",
                example = "vishwajeet@example.com"
        )
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @Schema(
                description = "Mobile number",
                example = "9876543210"
        )
        String phone,

        @Schema(
                description = "Department name",
                example = "IT"
        )
        String department,

        @Schema(
                description = "Employee designation",
                example = "Software Engineer"
        )
        String designation,

        @Schema(
                description = "Monthly salary",
                example = "65000"
        )
        @NotNull(message = "Salary is required")
        @Positive(message = "Salary must be greater than zero")
        BigDecimal salary,

        @Schema(
                description = "Joining date",
                example = "2026-07-05"
        )
        @NotNull(message = "Joining date is required")
        LocalDate joiningDate
) {}