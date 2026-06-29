package com.project.ems.mapper;

import com.project.ems.dto.EmployeeRequest;
import com.project.ems.dto.EmployeeResponse;
import com.project.ems.entity.Employee;
import org.springframework.stereotype.Component;

/**
 * Utility mapper class responsible for converting data between the
 * API data transfer layers (DTOs) and the database persistence layer (Entities).
 */
public class EmployeeMapper {

    /**
     * Private constructor to prevent instantiation.
     * This class functions strictly as a stateless utility class with static methods.
     */
    private EmployeeMapper() {}

    /**
     * Maps an incoming EmployeeRequest DTO into an Employee database entity.
     * Uses Lombok's Builder pattern for safe object construction.
     *
     * @param request the immutable Java Record containing client input
     * @return a new Employee entity populated with request data
     */
    public static Employee toEntity(EmployeeRequest request) {
        if (request == null) {
            return null;
        }

        return Employee.builder()
                // Java Records use direct method accessors (no 'get' prefix)
                .employeeCode(request.employeeCode())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .department(request.department())
                .designation(request.designation())
                .salary(request.salary())
                .joiningDate(request.joiningDate())
                // Business Logic: Automatically set new employees to active status
                .status(true)
                .build();
    }

    /**
     * Maps a database Employee entity into an outgoing EmployeeResponse DTO.
     * Uses the canonical constructor of the response record/DTO.
     *
     * @param employee the managed database entity
     * @return a populated EmployeeResponse DTO to return to the client
     */
    public static EmployeeResponse toResponse(Employee employee) {
        if (employee == null) {
            return null;
        }

        return new EmployeeResponse(
                // Standard entities use traditional Lombok/POJO JavaBean getters
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