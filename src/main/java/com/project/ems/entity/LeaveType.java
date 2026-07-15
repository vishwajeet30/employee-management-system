package com.project.ems.entity;

/**
 * Defines the leave types supported by the application.
 *
 * The module is intentionally kept small for the project MVP.
 */
public enum LeaveType {

    /**
     * Leave used for personal or urgent work.
     */
    CASUAL,

    /**
     * Leave used when the employee is unwell.
     */
    SICK,

    /**
     * Planned leave earned by the employee.
     */
    EARNED
}