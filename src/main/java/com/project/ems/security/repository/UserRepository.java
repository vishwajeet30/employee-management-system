package com.project.ems.security.repository;

import com.project.ems.security.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository responsible for User database operations.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Loads the user and their assigned role in the same query.
     *
     * The role is required while creating Spring Security authorities
     * such as ROLE_ADMIN and ROLE_EMPLOYEE.
     */
    @EntityGraph(attributePaths = "role")
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}