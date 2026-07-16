package com.project.ems.repository;

import com.project.ems.entity.LeaveApplication;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository responsible for LeaveApplication database operations.
 */
public interface LeaveRepository
        extends JpaRepository<LeaveApplication, Long> {

    /**
     * Retrieves one leave application together with its applicant.
     *
     * @EntityGraph prevents problems when accessing the lazily loaded User.
     */
    @Override
    @EntityGraph(attributePaths = "applicant")
    Optional<LeaveApplication> findById(Long id);

    /**
     * Retrieves all leave applications submitted by one user.
     *
     * Results are ordered from newest to oldest.
     */
    @EntityGraph(attributePaths = "applicant")
    List<LeaveApplication>
    findByApplicant_UsernameOrderByCreatedAtDesc(String username);

    /**
     * Retrieves all leave applications for ADMIN and HR.
     *
     * Results are ordered from newest to oldest.
     */
    @EntityGraph(attributePaths = "applicant")
    List<LeaveApplication> findAllByOrderByCreatedAtDesc();
}