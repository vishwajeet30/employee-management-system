package com.project.ems.dto;

import com.project.ems.entity.LeaveStatus;
import com.project.ems.entity.LeaveType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO containing leave application details.
 */
@Schema(
        name = "Leave Response",
        description = "Response containing leave application information."
)
public record LeaveResponse(

        @Schema(description = "Leave application ID", example = "1")
        Long id,

        @Schema(description = "Applicant username", example = "employee")
        String applicantUsername,

        @Schema(description = "Leave type", example = "CASUAL")
        LeaveType leaveType,

        @Schema(description = "Leave start date", example = "2026-07-20")
        LocalDate startDate,

        @Schema(description = "Leave end date", example = "2026-07-22")
        LocalDate endDate,

        @Schema(description = "Number of leave days", example = "3")
        long totalDays,

        @Schema(description = "Reason for leave")
        String reason,

        @Schema(description = "Current leave status", example = "PENDING")
        LeaveStatus status,

        @Schema(description = "Username of the reviewer")
        String reviewedBy,

        @Schema(description = "Review comment")
        String reviewComment,

        @Schema(description = "Time when the request was submitted")
        LocalDateTime createdAt

) {
}