package com.project.ems.repository;

import com.project.ems.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data Access Layer (Repository) for the Employee Entity.
 * * DESIGN HIGHLIGHTS:
 * - Extends JpaRepository to automatically inherit full CRUD capabilities (save, delete, findById, etc.).
 * - Generates actual database queries dynamically from method names via Spring Data JPA.
 * - Long specifies that the Primary Key (@Id) type of the Employee entity is a java.lang.Long.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Custom Query: Finds an employee by their unique employee code.
     * Wraps the result in an Optional to safely handle cases where the code doesn't exist.
     * (SQL behind the scenes: SELECT * FROM employees WHERE employee_code = ?)
     * * Note: Make sure the field name 'employeCode' matches the spelling in your Entity
     * exactly (your Entity uses 'employeeCode' with a double 'e').
     */
    Optional<Employee> findByEmployeeCode(String employeeCode);

    /**
     * Custom Query: Finds an employee by their unique email address.
     * Returns an Optional to protect against NullPointerExceptions if no match is found.
     * (SQL behind the scenes: SELECT * FROM employees WHERE email = ?)
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Validation Query: Checks if an email is already registered in the system.
     * Returns true if it exists, false if it doesn't. Great for signup/creation validation.
     * (SQL behind the scenes: SELECT COUNT(*) > 0 FROM employees WHERE email = ?)
     */
    boolean existsByEmail(String email);

    /**
     * Validation Query: Checks if an employee code is already taken.
     * Used to prevent duplicate system identifiers before saving a new record.
     * (SQL behind the scenes: SELECT COUNT(*) > 0 FROM employees WHERE employee_code = ?)
     */
    boolean existsByEmployeeCode(String employeeCode);

    Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable);
}