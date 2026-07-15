package com.project.ems.entity;

import com.project.ems.security.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing an employee leave application.
 *
 * Every leave application belongs to one authenticated User.
 */
@Entity
@Table(name = "leave_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApplication {

    /**
     * Database primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who submitted the leave application.
     *
     * Many leave applications can belong to one user.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    /**
     * Type of leave requested.
     *
     * EnumType.STRING stores values such as CASUAL and SICK
     * instead of numeric enum positions.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false, length = 20)
    private LeaveType leaveType;

    /**
     * First day of leave.
     */
    @Column(nullable = false)
    private LocalDate startDate;

    /**
     * Last day of leave.
     */
    @Column(nullable = false)
    private LocalDate endDate;

    /**
     * Reason supplied by the employee.
     */
    @Column(nullable = false, length = 500)
    private String reason;

    /**
     * Current approval status.
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeaveStatus status = LeaveStatus.PENDING;

    /**
     * Username of the ADMIN or HR user who reviewed the request.
     *
     * Keeping this as a String avoids creating another entity relationship
     * for this compact project.
     */
    @Column(length = 50)
    private String reviewedBy;

    /**
     * Optional comment added during approval or rejection.
     */
    @Column(length = 500)
    private String reviewComment;

    /**
     * Time when the leave application was created.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Time when the leave application was last updated.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Automatically sets timestamps before the first database insert.
     */
    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        // Every new leave application begins in PENDING status.
        if (status == null) {
            status = LeaveStatus.PENDING;
        }
    }

    /**
     * Automatically updates the modification timestamp.
     */
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}