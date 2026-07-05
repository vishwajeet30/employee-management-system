package com.project.ems.security.entity;

/**
 * Enum representing all supported roles in the application.
 *
 * Using an enum prevents typing mistakes and provides
 * compile-time safety throughout the project.
 */
public enum RoleType {

    /**
     * System administrator.
     * Has access to all modules and operations.
     */
    ADMIN,

    /**
     * Human Resources role.
     * Can manage employees and leave requests.
     */
    HR,

    /**
     * Normal employee.
     * Can manage only their own profile and leave requests.
     */
    EMPLOYEE
}