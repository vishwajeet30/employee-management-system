package com.project.ems.security.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user role.
 *
 * Examples:
 * ADMIN
 * HR
 * EMPLOYEE
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the role.
     *
     * Stored as String in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    private RoleType name;

    /**
     * Optional description of the role.
     */
    @Column(length = 255)
    private String description;

    /**
     * One role can belong to many users.
     *
     * mappedBy = "role"
     * refers to the "role" field inside the User entity.
     */
    @OneToMany(
            mappedBy = "role",
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<User> users = new ArrayList<>();
}