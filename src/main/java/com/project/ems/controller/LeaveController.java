package com.project.ems.controller;

import com.project.ems.dto.ApiResponse;
import com.project.ems.dto.LeaveApplyRequest;
import com.project.ems.dto.LeaveResponse;
import com.project.ems.dto.LeaveReviewRequest;
import com.project.ems.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller responsible for leave-management APIs.
 */
@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
@Tag(
        name = "Leave Management",
        description = "APIs for applying, viewing, approving and rejecting leave."
)
public class LeaveController {

    /**
     * Service containing leave business logic.
     */
    private final LeaveService leaveService;

    /**
     * Applies for leave for the currently authenticated user.
     *
     * Endpoint:
     * POST /api/v1/leaves
     */
    @Operation(summary = "Apply for leave")
    @PostMapping
    public ResponseEntity<ApiResponse<LeaveResponse>> applyLeave(
            @Valid @RequestBody LeaveApplyRequest request,
            Authentication authentication) {

        /*
         * authentication.getName() returns the username stored
         * in Spring Security's SecurityContext.
         */
        String username = authentication.getName();

        LeaveResponse leaveResponse =
                leaveService.applyLeave(username, request);

        ApiResponse<LeaveResponse> response =
                ApiResponse.<LeaveResponse>builder()
                        .success(true)
                        .message("Leave application submitted successfully.")
                        .data(leaveResponse)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Returns leave history for the currently authenticated user.
     *
     * Endpoint:
     * GET /api/v1/leaves/my
     */
    @Operation(summary = "View my leave applications")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getMyLeaves(
            Authentication authentication) {

        String username = authentication.getName();

        List<LeaveResponse> leaves =
                leaveService.getMyLeaves(username);

        ApiResponse<List<LeaveResponse>> response =
                ApiResponse.<List<LeaveResponse>>builder()
                        .success(true)
                        .message("Leave history retrieved successfully.")
                        .data(leaves)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Returns all leave applications.
     *
     * Only ADMIN and HR may access this endpoint.
     *
     * Endpoint:
     * GET /api/v1/leaves
     */
    @Operation(summary = "View all leave applications")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getAllLeaves() {

        List<LeaveResponse> leaves =
                leaveService.getAllLeaves();

        ApiResponse<List<LeaveResponse>> response =
                ApiResponse.<List<LeaveResponse>>builder()
                        .success(true)
                        .message("Leave applications retrieved successfully.")
                        .data(leaves)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Approves a pending leave application.
     *
     * Only ADMIN and HR may approve leave.
     *
     * Endpoint:
     * PUT /api/v1/leaves/{id}/approve
     */
    @Operation(summary = "Approve leave application")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<LeaveResponse>> approveLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveReviewRequest request,
            Authentication authentication) {

        String reviewerUsername = authentication.getName();

        LeaveResponse leaveResponse =
                leaveService.approveLeave(
                        id,
                        reviewerUsername,
                        request
                );

        ApiResponse<LeaveResponse> response =
                ApiResponse.<LeaveResponse>builder()
                        .success(true)
                        .message("Leave application approved successfully.")
                        .data(leaveResponse)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Rejects a pending leave application.
     *
     * Only ADMIN and HR may reject leave.
     *
     * Endpoint:
     * PUT /api/v1/leaves/{id}/reject
     */
    @Operation(summary = "Reject leave application")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<LeaveResponse>> rejectLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveReviewRequest request,
            Authentication authentication) {

        String reviewerUsername = authentication.getName();

        LeaveResponse leaveResponse =
                leaveService.rejectLeave(
                        id,
                        reviewerUsername,
                        request
                );

        ApiResponse<LeaveResponse> response =
                ApiResponse.<LeaveResponse>builder()
                        .success(true)
                        .message("Leave application rejected successfully.")
                        .data(leaveResponse)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }
}