package com.project.ems.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Request DTO used by ADMIN or HR while approving
 * or rejecting a leave application.
 */
@Schema(
        name = "Leave Review Request",
        description = "Optional comment submitted during leave review."
)
public record LeaveReviewRequest(

        @Schema(
                description = "Approval or rejection comment",
                example = "Approved after reviewing the project schedule."
        )
        @Size(
                max = 500,
                message = "Review comment cannot exceed 500 characters"
        )
        String comment

) {
}