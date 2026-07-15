package com.project.ems.entity;

/**
 * Represents the current status of a leave application.
 */
public enum LeaveStatus {

    /**
     * Leave has been submitted but not reviewed.
     */
    PENDING,

    /**
     * Leave has been approved by HR or ADMIN.
     */
    APPROVED,

    /**
     * Leave has been rejected by HR or ADMIN.
     */
    REJECTED
}