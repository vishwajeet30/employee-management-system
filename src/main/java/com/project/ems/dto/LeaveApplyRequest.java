package com.project.ems.dto;

import com.project.ems.entity.LeaveType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request DTO used when an authenticated employee applies for leave.
 */
@Schema(
        name = "Leave Apply Request",
        description = "Request payload used to submit a leave application."
)
public record LeaveApplyRequest(

        @Schema(
                description = "Type of leave",
                example = "CASUAL"
        )
        @NotNull(message = "Leave type is required")
        LeaveType leaveType,

        @Schema(
                description = "First date of leave",
                example = "2026-07-20"
        )
        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date cannot be in the past")
        LocalDate startDate,

        @Schema(
                description = "Last date of leave",
                example = "2026-07-22"
        )
        @NotNull(message = "End date is required")
        @FutureOrPresent(message = "End date cannot be in the past")
        LocalDate endDate,

        @Schema(
                description = "Reason for requesting leave",
                example = "Personal work"
        )
        @NotBlank(message = "Leave reason is required")
        @Size(
                max = 500,
                message = "Leave reason cannot exceed 500 characters"
        )
        String reason

) {
}