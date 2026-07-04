package com.project.ems.controller;

import com.project.ems.dto.ApiResponse;
import com.project.ems.dto.EmployeeResponse;
import com.project.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request){
        EmployeeResponse employee = employeeService.createEmployee(request);

        ApiResponse<EmployeeResponse> response = ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee created successfully.")
                .data(employee)
                .timestamp(LocalDateTime.now())
                .build();

        //Return HTTP 201 created
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}
