package com.project.ems.controller;

import com.project.ems.dto.ApiResponse;
import com.project.ems.dto.EmployeeRequest;
import com.project.ems.dto.EmployeeResponse;
import com.project.ems.entity.Employee;
import com.project.ems.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
                .message("Employee created successfully")
                .data(employee)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //___________________________________________________________________________________________________

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(
            @PathVariable Long id){
        EmployeeResponse employee = employeeService.getEmployeeById(id);

        ApiResponse<EmployeeResponse> response = ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee Retrieved Successfully")
                .data(employee)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    //**************************************************************************************************************

    @GetMapping("/code/{employeeCode}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmployeeId(
            @PathVariable String employeeCode){
        EmployeeResponse employee = employeeService.getEmployeeByEmployeeCode(employeeCode);

        ApiResponse<EmployeeResponse> response = ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee retrieved successfully")
                .data(employee)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
//**********************************************************************************************************************

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction){
        Page<EmployeeResponse> employees = employeeService.getAllEmployees(page,size,sortBy,direction);

        ApiResponse<Page<EmployeeResponse>> response = ApiResponse.<Page<EmployeeResponse>>builder()
                .success(true)
                .message("Employee retrieved Successfully")
                .data(employees)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    //******************************************************************************************************************

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> searchEmployees(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Page<EmployeeResponse> employees = employeeService.searchEmployees(keyword,page,size);

        ApiResponse<Page<EmployeeResponse>> response = ApiResponse.<Page<EmployeeResponse>>builder()
                .success(true)
                .message("Search completed succesfully")
                .data(employees)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/id")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request){
        EmployeeResponse employee = employeeService.updateEmployee(id, request);

        ApiResponse<EmployeeResponse> response = ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee updated successfully")
                .data(employee)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    //******************************************************************************************************************

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
            @PathVariable Long id){
        employeeService.deleteEmployee(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Employee deleted successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

}
