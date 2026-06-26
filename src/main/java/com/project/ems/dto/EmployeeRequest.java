package com.project.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record EmployeeRequest (
    @NotBlank(message = "Employee code is required")
    String employeeCode,
    @NotBlank(message = "First Name is required")
    String firstName,
    @NotBlank(message = "Last Name is required")
    String lastName,
    @Email
    @NotBlank
    String email,
    String phone,
    String departmemnt,
    String designation,

    @NotNull
    @Positive
    BigDecimal salary,
    @NotNull
    LocalDate joiningDate

    ) {}
