package com.project.ems.service.impl;

import com.project.ems.dto.EmployeeRequest;
import com.project.ems.dto.EmployeeResponse;
import com.project.ems.entity.Employee;
import com.project.ems.exception.DuplicateResourceException;
import com.project.ems.exception.ResourceNotFoundException;
import com.project.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployeeServiceImpl.
 *
 * These tests verify the service-layer business logic without:
 * - Starting the Spring Boot application
 * - Connecting to MySQL
 * - Calling the REST controller
 *
 * EmployeeRepository is replaced with a Mockito mock.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    /**
     * Mock repository used to simulate database behaviour.
     */
    @Mock
    private EmployeeRepository employeeRepository;

    /**
     * Real EmployeeServiceImpl object under test.
     *
     * Mockito automatically injects the mocked repository
     * into this service.
     */
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    /**
     * Verifies that a valid employee can be created successfully.
     */
    @Test
    void createEmployee_shouldCreateEmployeeSuccessfully() {

        // Arrange: create the incoming request DTO.
        EmployeeRequest request = createEmployeeRequest();

        /*
         * Simulate that neither the employee code nor email
         * exists in the database.
         */
        when(employeeRepository.existsByEmployeeCode("EMP001"))
                .thenReturn(false);

        when(employeeRepository.existsByEmail("rahul@example.com"))
                .thenReturn(false);

        /*
         * Simulate repository.save().
         *
         * The mapper creates an Employee without an ID.
         * The database normally generates the ID, so the mock
         * assigns ID 1 before returning the entity.
         */
        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> {

                    Employee employee = invocation.getArgument(0);
                    employee.setId(1L);

                    return employee;
                });

        // Act: execute the service method.
        EmployeeResponse response =
                employeeService.createEmployee(request);

        // Assert: verify the returned employee information.
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("EMP001", response.employeeCode());
        assertEquals("Rahul", response.firstName());
        assertEquals("Sharma", response.lastName());
        assertEquals("rahul@example.com", response.email());
        assertEquals(new BigDecimal("50000"), response.salary());

        /*
         * Verify that save() was called exactly once.
         */
        verify(employeeRepository, times(1))
                .save(any(Employee.class));
    }

    /**
     * Verifies that registration fails when the employee code
     * already belongs to another employee.
     */
    @Test
    void createEmployee_shouldThrowExceptionWhenEmployeeCodeExists() {

        // Arrange
        EmployeeRequest request = createEmployeeRequest();

        // Simulate duplicate employee code.
        when(employeeRepository.existsByEmployeeCode("EMP001"))
                .thenReturn(true);

        // Act and Assert
        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> employeeService.createEmployee(request)
                );

        assertEquals(
                "Employee code already exists: EMP001",
                exception.getMessage()
        );

        /*
         * The service must not check email or save anything
         * after detecting the duplicate employee code.
         */
        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    /**
     * Verifies that registration fails when the email
     * already belongs to another employee.
     */
    @Test
    void createEmployee_shouldThrowExceptionWhenEmailExists() {

        // Arrange
        EmployeeRequest request = createEmployeeRequest();

        when(employeeRepository.existsByEmail("rahul@example.com"))
                .thenReturn(true);

        // Act and Assert
        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> employeeService.createEmployee(request)
                );

        assertEquals(
                "Email already exists: rahul@example.com",
                exception.getMessage()
        );

        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    /**
     * Verifies successful retrieval of an employee by database ID.
     */
    @Test
    void getEmployeeById_shouldReturnEmployeeWhenFound() {

        // Arrange
        Employee employee = createEmployeeEntity();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        // Act
        EmployeeResponse response =
                employeeService.getEmployeeById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("EMP001", response.employeeCode());
        assertEquals("rahul@example.com", response.email());

        verify(employeeRepository, times(1))
                .findById(1L);
    }

    /**
     * Verifies that a meaningful exception is thrown
     * when the requested employee does not exist.
     */
    @Test
    void getEmployeeById_shouldThrowExceptionWhenEmployeeNotFound() {

        // Arrange: simulate an empty database result.
        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> employeeService.getEmployeeById(99L)
                );

        assertEquals(
                "Employee not found with id: 99",
                exception.getMessage()
        );

        verify(employeeRepository, times(1))
                .findById(99L);
    }

    /**
     * Creates reusable employee request data for the tests.
     */
    private EmployeeRequest createEmployeeRequest() {

        return new EmployeeRequest(
                "EMP001",
                "Rahul",
                "Sharma",
                "rahul@example.com",
                "9876543210",
                "IT",
                "Software Engineer",
                new BigDecimal("50000"),
                LocalDate.of(2026, 7, 16)
        );
    }

    /**
     * Creates a reusable Employee entity representing
     * an existing database record.
     */
    private Employee createEmployeeEntity() {

        return Employee.builder()
                .id(1L)
                .employeeCode("EMP001")
                .firstName("Rahul")
                .lastName("Sharma")
                .email("rahul@example.com")
                .phone("9876543210")
                .department("IT")
                .designation("Software Engineer")
                .salary(new BigDecimal("50000"))
                .joiningDate(LocalDate.of(2026, 7, 16))
                .status(true)
                .build();
    }
}