package com.project.ems.service;

import com.project.ems.dto.LeaveApplyRequest;
import com.project.ems.dto.LeaveResponse;
import com.project.ems.dto.LeaveReviewRequest;

import java.util.List;

/**
 * Service contract for leave-management operations.
 */
public interface LeaveService {

    /**
     * Submits a leave request for the authenticated user.
     *
     * @param username authenticated username
     * @param request leave details
     * @return submitted leave application
     */
    LeaveResponse applyLeave(
            String username,
            LeaveApplyRequest request
    );

    /**
     * Returns leave applications submitted by the authenticated user.
     *
     * @param username authenticated username
     * @return user's leave history
     */
    List<LeaveResponse> getMyLeaves(String username);

    /**
     * Returns all leave applications.
     *
     * Intended for ADMIN and HR users.
     *
     * @return all leave applications
     */
    List<LeaveResponse> getAllLeaves();

    /**
     * Approves a pending leave application.
     *
     * @param leaveId leave application ID
     * @param reviewerUsername ADMIN or HR username
     * @param request optional review comment
     * @return approved leave application
     */
    LeaveResponse approveLeave(
            Long leaveId,
            String reviewerUsername,
            LeaveReviewRequest request
    );

    /**
     * Rejects a pending leave application.
     *
     * @param leaveId leave application ID
     * @param reviewerUsername ADMIN or HR username
     * @param request optional review comment
     * @return rejected leave application
     */
    LeaveResponse rejectLeave(
            Long leaveId,
            String reviewerUsername,
            LeaveReviewRequest request
    );
}