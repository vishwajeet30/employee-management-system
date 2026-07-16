package com.project.ems.mapper;

import com.project.ems.dto.LeaveApplyRequest;
import com.project.ems.dto.LeaveResponse;
import com.project.ems.entity.LeaveApplication;
import com.project.ems.entity.LeaveStatus;
import com.project.ems.security.entity.User;

import java.time.temporal.ChronoUnit;

/**
 * Utility class responsible for converting leave DTOs and entities.
 */
public final class LeaveMapper {

    /**
     * Private constructor prevents object creation.
     *
     * All methods in this class are static.
     */
    private LeaveMapper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts a leave request into a LeaveApplication entity.
     *
     * @param request leave request received from the client
     * @param applicant authenticated user applying for leave
     * @return new leave application entity
     */
    public static LeaveApplication toEntity(
            LeaveApplyRequest request,
            User applicant) {

        return LeaveApplication.builder()
                .applicant(applicant)
                .leaveType(request.leaveType())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .reason(request.reason())
                .status(LeaveStatus.PENDING)
                .build();
    }

    /**
     * Converts a LeaveApplication entity into a response DTO.
     *
     * @param leaveApplication leave entity
     * @return API response containing leave details
     */
    public static LeaveResponse toResponse(
            LeaveApplication leaveApplication) {

        /*
         * ChronoUnit calculates the difference between dates.
         * We add 1 so both the start and end dates are included.
         *
         * Example:
         * July 20 to July 22 = 3 leave days.
         */
        long totalDays = ChronoUnit.DAYS.between(
                leaveApplication.getStartDate(),
                leaveApplication.getEndDate()
        ) + 1;

        return new LeaveResponse(
                leaveApplication.getId(),
                leaveApplication.getApplicant().getUsername(),
                leaveApplication.getLeaveType(),
                leaveApplication.getStartDate(),
                leaveApplication.getEndDate(),
                totalDays,
                leaveApplication.getReason(),
                leaveApplication.getStatus(),
                leaveApplication.getReviewedBy(),
                leaveApplication.getReviewComment(),
                leaveApplication.getCreatedAt()
        );
    }
}