package com.project.ems.service.impl;

import com.project.ems.dto.LeaveApplyRequest;
import com.project.ems.dto.LeaveResponse;
import com.project.ems.dto.LeaveReviewRequest;
import com.project.ems.entity.LeaveApplication;
import com.project.ems.entity.LeaveStatus;
import com.project.ems.exception.BadRequestException;
import com.project.ems.exception.ResourceNotFoundException;
import com.project.ems.mapper.LeaveMapper;
import com.project.ems.repository.LeaveRepository;
import com.project.ems.security.entity.User;
import com.project.ems.security.repository.UserRepository;
import com.project.ems.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation containing leave-management business logic.
 */
@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    /**
     * Repository used for leave database operations.
     */
    private final LeaveRepository leaveRepository;

    /**
     * Repository used to identify the authenticated user.
     */
    private final UserRepository userRepository;

    /**
     * Submits a new leave application.
     */
    @Override
    @Transactional
    public LeaveResponse applyLeave(
            String username,
            LeaveApplyRequest request) {

        // End date cannot occur before the start date.
        validateLeaveDates(request);

        // Find the authenticated user in the database.
        User applicant = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with username: " + username
                ));

        // Convert the request into a database entity.
        LeaveApplication leaveApplication =
                LeaveMapper.toEntity(request, applicant);

        // Save the leave application.
        LeaveApplication savedLeave =
                leaveRepository.save(leaveApplication);

        // Return the response DTO.
        return LeaveMapper.toResponse(savedLeave);
    }

    /**
     * Returns leave applications belonging to the logged-in user.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LeaveResponse> getMyLeaves(String username) {

        return leaveRepository
                .findByApplicant_UsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(LeaveMapper::toResponse)
                .toList();
    }

    /**
     * Returns all leave applications for ADMIN and HR.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LeaveResponse> getAllLeaves() {

        return leaveRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(LeaveMapper::toResponse)
                .toList();
    }

    /**
     * Approves a leave application.
     */
    @Override
    @Transactional
    public LeaveResponse approveLeave(
            Long leaveId,
            String reviewerUsername,
            LeaveReviewRequest request) {

        return reviewLeave(
                leaveId,
                reviewerUsername,
                request,
                LeaveStatus.APPROVED
        );
    }

    /**
     * Rejects a leave application.
     */
    @Override
    @Transactional
    public LeaveResponse rejectLeave(
            Long leaveId,
            String reviewerUsername,
            LeaveReviewRequest request) {

        return reviewLeave(
                leaveId,
                reviewerUsername,
                request,
                LeaveStatus.REJECTED
        );
    }

    /**
     * Shared method used for both approval and rejection.
     *
     * This avoids duplicating the same business logic.
     */
    private LeaveResponse reviewLeave(
            Long leaveId,
            String reviewerUsername,
            LeaveReviewRequest request,
            LeaveStatus newStatus) {

        // Find the leave application.
        LeaveApplication leaveApplication =
                leaveRepository.findById(leaveId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Leave application not found with id: "
                                                + leaveId
                                )
                        );

        /*
         * Only pending leave applications may be reviewed.
         * An approved or rejected request cannot be reviewed again.
         */
        if (leaveApplication.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException(
                    "Leave application has already been reviewed."
            );
        }

        // Update review information.
        leaveApplication.setStatus(newStatus);
        leaveApplication.setReviewedBy(reviewerUsername);
        leaveApplication.setReviewComment(request.comment());

        // Persist the updated leave status.
        LeaveApplication reviewedLeave =
                leaveRepository.save(leaveApplication);

        return LeaveMapper.toResponse(reviewedLeave);
    }

    /**
     * Verifies that the leave date range is valid.
     */
    private void validateLeaveDates(LeaveApplyRequest request) {

        if (request.endDate().isBefore(request.startDate())) {
            throw new BadRequestException(
                    "Leave end date cannot be before the start date."
            );
        }
    }
}