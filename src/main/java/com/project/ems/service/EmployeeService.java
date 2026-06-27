package com.project.ems.service;

import com.project.ems.dto.EmployeeRequest;
import com.project.ems.dto.EmployeeResponse;
import org.springframework.data.domain.Page;

/**
 * Service interface defining the business logic operations for managing Employees.
 */
public interface EmployeeService {

    /**
     * Creates and saves a new employee in the system.
     *
     * @param request the DTO containing the new employee's details
     * @return the structured response details of the created employee
     */
    EmployeeResponse createEmployee(EmployeeRequest request);

    /**
     * Updates the details of an existing employee.
     *
     * @param id the unique database ID of the employee to update
     * @param request the DTO containing the updated details
     * @return the structured response details of the updated employee
     */
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);

    /**
     * Retrieves a single employee by their database primary key.
     *
     * @param id the unique database ID of the employee
     * @return the details of the found employee
     */
    EmployeeResponse getEmployeeById(Long id);

    /**
     * Retrieves a single employee by their unique business employee code (e.g., EMP001).
     *
     * @param employeeCode the unique business string identifier of the employee
     * @return the details of the found employee
     */
    EmployeeResponse getEmployeeByEmployeeCode(String employeeCode);

    /**
     * Retrieves a paginated and sorted list of all employees.
     *
     * @param page the zero-based page index to retrieve (starts at 0)
     * @param size the number of records per page
     * @param sortBy the field name to sort the results by (e.g., "lastName")
     * @param direction the direction of sorting, typically "ASC" or "DESC"
     * @return a Page container holding the list of employees and pagination metadata
     */
    Page<EmployeeResponse> getAllEmployees(
            int page,
            int size,
            String sortBy,
            String direction);

    /**
     * Searches for employees using a keyword filter against their attributes (like name or email).
     *
     * @param keyword the search term to match against employee fields
     * @param page the zero-based page index to retrieve
     * @param size the number of records per page
     * @return a Page container holding the matching employees
     */
    Page<EmployeeResponse> searchEmployees(
            String keyword,
            int page,
            int size);

    /**
     * Removes an employee from the system by their unique database ID.
     *
     * @param id the unique database ID of the employee to delete
     */
    void deleteEmployee(Long id);
}