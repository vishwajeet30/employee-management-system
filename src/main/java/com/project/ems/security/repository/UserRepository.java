package com.project.ems.security.repository;

import com.project.ems.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface responsible for all
 * database operations related to User.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     *
     * Used during authentication.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     *
     * Useful for password reset
     * and account verification.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a username already exists.
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether an email already exists.
     */
    boolean existsByEmail(String email);
}