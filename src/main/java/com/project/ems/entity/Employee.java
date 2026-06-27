package com.project.ems.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Database Entity representing the 'employees' table.
 *
 * DESIGN HIGHLIGHTS:
 * - Uses Lombok to automatically generate boilerplate code (getters, setters, constructors).
 * - Maps to relational database storage using Jakarta Persistence (JPA / Hibernate).
 * - Implements automated auditing timestamps via JPA lifecycle hooks (@PrePersist / @PreUpdate).
 */
@Entity
@Table(name = "employees") // Explicitly names the database table 'employees' (plural)
@Getter                     // Lombok: Generates public getX() methods for all fields
@Setter                     // Lombok: Generates public setX() methods for all fields
@NoArgsConstructor          // Lombok: Generates empty default constructor (Required by Hibernate)
@AllArgsConstructor         // Lombok: Generates a constructor with all fields (Required by @Builder)
@Builder                    // Lombok: Enables fluent API creation syntax: Employee.builder().firstName("...").build()
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments the ID column (like SERIAL in PostgreSQL/MySQL)
    private Long id;

    @Column(nullable = false, unique = true) // Enforces a NOT NULL and UNIQUE constraint in the database
    private String employeeCode;

    @Column(nullable = false)                // Required field; cannot be empty/null in the DB
    private String firstName;

    @Column(nullable = false)                // Required field; cannot be empty/null in the DB
    private String lastName;

    @Column(nullable = false, unique = true) // Ensures no two employees can register with the same email
    private String email;

    private String phone;

    private String department;

    private String designation;

    @Column(nullable = false)                // Uses BigDecimal to protect financial figures from floating-point rounding errors
    private BigDecimal salary;

    private LocalDate joiningDate;              // Stored as a String (Tip: consider changing to LocalDate later for better queries)
    @Builder.Default
    private Boolean status = true;           // Application-level default value (defaults to 'true' / active upon creation)

    private LocalDateTime createdAt;         // Audit field tracking when the employee record was first made

    private LocalDateTime updatedAt;         // Audit field tracking the most recent modification timestamp

    /**
     * JPA Lifecycle Hook
     * Runs automatically RIGHT BEFORE this record is inserted into the database for the first time.
     */
    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * JPA Lifecycle Hook
     * Runs automatically EVERY TIME this record is modified/updated in the database.
     */
    @PreUpdate
    public void preUpdate(){
        updatedAt = LocalDateTime.now();
    }
}