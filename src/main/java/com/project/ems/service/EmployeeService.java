package com.project.ems.service;

import com.project.ems.dto.EmployeeRequest;
import com.project.ems.dto.EmployeeResponse;
import org.springframework.data.domain.Page;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse getEmployeeByEmployeeCode(String employeeCode);

    Page<EmployeeResponse> getAllEmployees(
            int page,
            int size,
            String sortBy,
            String direction);
    Page<EmployeeResponse> searchEmployees(
      String keyword,
      int page,
      int size);
    void deleteEmplyee(Long id);



}
