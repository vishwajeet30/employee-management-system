package com.project.ems.service.impl;

import com.project.ems.dto.LeaveApplyRequest;
import com.project.ems.dto.LeaveResponse;
import com.project.ems.dto.LeaveReviewRequest;
import com.project.ems.entity.LeaveApplication;
import com.project.ems.entity.LeaveStatus;
import com.project.ems.entity.LeaveType;
import com.project.ems.exception.BadRequestException;
import com.project.ems.repository.LeaveRepository;
import com.project.ems.security.entity.User;
import com.project.ems.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LeaveServiceImpl.
 *
 * These tests verify leave-management business logic without:
 * - Starting Spring Boot
 * - Connecting to MySQL
 * - Calling the REST controller
 *
 * LeaveRepository and UserRepository are replaced with Mockito mocks.
 */
@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {

    /**
     * Mock repository used for leave database operations.
     */
    @Mock
    private LeaveRepository leaveRepository;

    /**
     * Mock repository used for finding the authenticated user.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Real LeaveServiceImpl object under test.
     *
     * Mockito injects the mocked repositories automatically.
     */
    @InjectMocks
    private LeaveServiceImpl leaveService;

    /**
     * Verifies that an employee can successfully apply for leave.
     */
    @Test
    void applyLeave_shouldCreateLeaveApplicationSuccessfully() {

        // Arrange
        User applicant = createEmployeeUser();

        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = startDate.plusDays(2);

        LeaveApplyRequest request = new LeaveApplyRequest(
                LeaveType.CASUAL,
                startDate,
                endDate,
                "Personal work"
        );

        // Simulate finding the authenticated user.
        when(userRepository.findByUsername("employee"))
                .thenReturn(Optional.of(applicant));

        /*
         * Simulate saving the leave application.
         *
         * The database normally generates the ID and timestamps.
         * The mock assigns them before returning the entity.
         */
        when(leaveRepository.save(any(LeaveApplication.class)))
                .thenAnswer(invocation -> {

                    LeaveApplication leave =
                            invocation.getArgument(0);

                    leave.setId(1L);
                    leave.setCreatedAt(LocalDateTime.now());
                    leave.setUpdatedAt(LocalDateTime.now());

                    return leave;
                });

        // Act
        LeaveResponse response =
                leaveService.applyLeave("employee", request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("employee", response.applicantUsername());
        assertEquals(LeaveType.CASUAL, response.leaveType());
        assertEquals(startDate, response.startDate());
        assertEquals(endDate, response.endDate());

        /*
         * Start date and end date are both included:
         * Day 1 + Day 2 + Day 3 = 3 days.
         */
        assertEquals(3, response.totalDays());

        assertEquals("Personal work", response.reason());
        assertEquals(LeaveStatus.PENDING, response.status());
        assertNull(response.reviewedBy());

        verify(userRepository, times(1))
                .findByUsername("employee");

        verify(leaveRepository, times(1))
                .save(any(LeaveApplication.class));
    }

    /**
     * Verifies that a leave request is rejected when
     * the end date is before the start date.
     */
    @Test
    void applyLeave_shouldThrowExceptionWhenDateRangeIsInvalid() {

        // Arrange
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(2);

        LeaveApplyRequest request = new LeaveApplyRequest(
                LeaveType.SICK,
                startDate,
                endDate,
                "Medical rest"
        );

        // Act and Assert
        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> leaveService.applyLeave(
                                "employee",
                                request
                        )
                );

        assertEquals(
                "Leave end date cannot be before the start date.",
                exception.getMessage()
        );

        /*
         * Validation occurs before accessing the database,
         * so neither repository should be called.
         */
        verifyNoInteractions(userRepository);
        verifyNoInteractions(leaveRepository);
    }

    /**
     * Verifies that an employee can retrieve their own leave history.
     */
    @Test
    void getMyLeaves_shouldReturnEmployeeLeaveHistory() {

        // Arrange
        User applicant = createEmployeeUser();

        LeaveApplication firstLeave = createLeaveApplication(
                1L,
                applicant,
                LeaveType.CASUAL,
                LeaveStatus.PENDING
        );

        LeaveApplication secondLeave = createLeaveApplication(
                2L,
                applicant,
                LeaveType.SICK,
                LeaveStatus.APPROVED
        );

        when(leaveRepository
                .findByApplicant_UsernameOrderByCreatedAtDesc("employee"))
                .thenReturn(List.of(secondLeave, firstLeave));

        // Act
        List<LeaveResponse> responses =
                leaveService.getMyLeaves("employee");

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());

        // Repository returns newest leave first.
        assertEquals(2L, responses.get(0).id());
        assertEquals(LeaveStatus.APPROVED, responses.get(0).status());

        assertEquals(1L, responses.get(1).id());
        assertEquals(LeaveStatus.PENDING, responses.get(1).status());

        verify(leaveRepository, times(1))
                .findByApplicant_UsernameOrderByCreatedAtDesc(
                        "employee"
                );
    }

    /**
     * Verifies that ADMIN or HR can approve a pending leave request.
     */
    @Test
    void approveLeave_shouldApprovePendingLeaveSuccessfully() {

        // Arrange
        User applicant = createEmployeeUser();

        LeaveApplication leaveApplication =
                createLeaveApplication(
                        1L,
                        applicant,
                        LeaveType.EARNED,
                        LeaveStatus.PENDING
                );

        LeaveReviewRequest request =
                new LeaveReviewRequest("Approved.");

        when(leaveRepository.findById(1L))
                .thenReturn(Optional.of(leaveApplication));

        when(leaveRepository.save(any(LeaveApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LeaveResponse response =
                leaveService.approveLeave(
                        1L,
                        "admin",
                        request
                );

        // Assert
        assertEquals(LeaveStatus.APPROVED, response.status());
        assertEquals("admin", response.reviewedBy());
        assertEquals("Approved.", response.reviewComment());

        verify(leaveRepository, times(1))
                .findById(1L);

        verify(leaveRepository, times(1))
                .save(leaveApplication);
    }

    /**
     * Verifies that an already reviewed leave application
     * cannot be approved or rejected again.
     */
    @Test
    void approveLeave_shouldThrowExceptionWhenLeaveAlreadyReviewed() {

        // Arrange
        User applicant = createEmployeeUser();

        LeaveApplication leaveApplication =
                createLeaveApplication(
                        1L,
                        applicant,
                        LeaveType.CASUAL,
                        LeaveStatus.APPROVED
                );

        LeaveReviewRequest request =
                new LeaveReviewRequest("Approve again");

        when(leaveRepository.findById(1L))
                .thenReturn(Optional.of(leaveApplication));

        // Act and Assert
        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> leaveService.approveLeave(
                                1L,
                                "admin",
                                request
                        )
                );

        assertEquals(
                "Leave application has already been reviewed.",
                exception.getMessage()
        );

        /*
         * The application should not be saved because
         * its status was already reviewed.
         */
        verify(leaveRepository, never())
                .save(any(LeaveApplication.class));
    }

    /**
     * Creates a reusable employee user for leave tests.
     */
    private User createEmployeeUser() {

        return User.builder()
                .id(1L)
                .username("employee")
                .email("employee@example.com")
                .password("$2a$10$encodedPassword")
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }

    /**
     * Creates a reusable leave application entity.
     */
    private LeaveApplication createLeaveApplication(
            Long id,
            User applicant,
            LeaveType leaveType,
            LeaveStatus status) {

        LocalDate startDate = LocalDate.now().plusDays(2);

        return LeaveApplication.builder()
                .id(id)
                .applicant(applicant)
                .leaveType(leaveType)
                .startDate(startDate)
                .endDate(startDate.plusDays(2))
                .reason("Personal requirement")
                .status(status)
                .reviewedBy(
                        status == LeaveStatus.PENDING
                                ? null
                                : "admin"
                )
                .reviewComment(
                        status == LeaveStatus.PENDING
                                ? null
                                : "Reviewed"
                )
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}