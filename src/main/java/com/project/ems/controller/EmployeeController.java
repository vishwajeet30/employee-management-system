package com.project.ems.controller;

import com.project.ems.dto.ApiResponse;
import com.project.ems.dto.EmployeeRequest;
import com.project.ems.dto.EmployeeResponse;
import com.project.ems.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST Controller responsible for handling HTTP requests related to Employees.
 *
 * This layer should contain only request/response handling.
 * All business logic is delegated to the Service layer.
 */
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    /**
     * Spring injects EmployeeService using constructor injection.
     * Lombok's @RequiredArgsConstructor generates the constructor automatically.
     */
    private final EmployeeService employeeService;

    /**
     * Creates a new employee.
     *
     * @param request Employee details received in the request body.
     *                @Valid triggers Bean Validation before entering the service layer.
     * @return Created employee wrapped inside a standardized API response.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {

        // Delegate business logic to service layer
        EmployeeResponse employee = employeeService.createEmployee(request);

        // Build standardized API response
        ApiResponse<EmployeeResponse> response = ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee created successfully.")
                .data(employee)
                .timestamp(LocalDateTime.now())
                .build();

        // Return HTTP 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves an employee by database ID.
     *
     * Example:
     * GET /api/v1/employees/1
     *
     * @param id Employee primary key.
     * @return Employee details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(
            @PathVariable Long id) {

        EmployeeResponse employee = employeeService.getEmployeeById(id);

        ApiResponse<EmployeeResponse> response = ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee retrieved successfully.")
                .data(employee)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves an employee using their business employee code.
     *
     * Example:
     * GET /api/v1/employees/code/EMP001
     *
     * @param employeeCode Unique employee code.
     * @return Employee details.
     */
    @GetMapping("/code/{employeeCode}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmployeeCode(
            @PathVariable String employeeCode) {

        EmployeeResponse employee =
                employeeService.getEmployeeByEmployeeCode(employeeCode);

        ApiResponse<EmployeeResponse> response = ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee retrieved successfully.")
                .data(employee)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves employees with pagination and sorting.
     *
     * Example:
     * GET /api/v1/employees?page=0&size=10&sortBy=firstName&direction=ASC
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "id") String sortBy,

            @RequestParam(defaultValue = "ASC") String direction) {

        Page<EmployeeResponse> employees =
                employeeService.getAllEmployees(page, size, sortBy, direction);

        ApiResponse<Page<EmployeeResponse>> response =
                ApiResponse.<Page<EmployeeResponse>>builder()
                        .success(true)
                        .message("Employees retrieved successfully.")
                        .data(employees)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Searches employees using a keyword.
     *
     * Example:
     * GET /api/v1/employees/search?keyword=john&page=0&size=5
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> searchEmployees(

            @RequestParam String keyword,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size) {

        Page<EmployeeResponse> employees =
                employeeService.searchEmployees(keyword, page, size);

        ApiResponse<Page<EmployeeResponse>> response =
                ApiResponse.<Page<EmployeeResponse>>builder()
                        .success(true)
                        .message("Search completed successfully.")
                        .data(employees)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing employee.
     *
     * @param id Database ID of employee.
     * @param request Updated employee information.
     * @return Updated employee.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(

            @PathVariable Long id,

            @Valid @RequestBody EmployeeRequest request) {

        EmployeeResponse employee =
                employeeService.updateEmployee(id, request);

        ApiResponse<EmployeeResponse> response =
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee updated successfully.")
                        .data(employee)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an employee.
     *
     * Example:
     * DELETE /api/v1/employees/1
     *
     * @param id Database ID of employee.
     * @return Success message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
            @PathVariable Long id) {

        employeeService.deleteEmployee(id);

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Employee deleted successfully.")
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }
}