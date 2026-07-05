package com.project.ems.security.repository;

import com.project.ems.security.entity.Role;
import com.project.ems.security.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for performing database operations
 * on the Role entity.
 *
 * JpaRepository provides:
 * - save()
 * - findById()
 * - findAll()
 * - delete()
 * - existsById()
 * and many more.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its enum value.
     *
     * Example:
     * RoleType.ADMIN
     */
    Optional<Role> findByName(RoleType name);

    /**
     * Checks whether a role already exists.
     */
    boolean existsByName(RoleType name);
}