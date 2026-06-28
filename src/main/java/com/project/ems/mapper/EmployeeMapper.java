package com.project.ems.mapper;

import com.project.ems.dto.EmployeeRequest;
import com.project.ems.dto.EmployeeResponse;
import com.project.ems.entity.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class EmployeeMapper {
    private EmployeeMapper(){}
    public static Employee toEntity(EmployeeRequest request){

        return Employee.builder()
                .employeeCode(request.employeeCode())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .department(request.department())
                .designation(request.designation())
                .salary(request.salary())
                .joiningDate(request.joiningDate())
                .status(true)
                .build();
    }

    public static EmployeeResponse toResponse(Employee employee){
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getSalary(),
                employee.getJoiningDate(),
                employee.getStatus()
        );
    }
}

