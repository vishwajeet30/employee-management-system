package com.project.ems.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeResponse (
        Long id,
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String phone,
        String department,
        String designation,
        BigDecimal salary,
        LocalDate joiningDate,
        Boolean status
){}
