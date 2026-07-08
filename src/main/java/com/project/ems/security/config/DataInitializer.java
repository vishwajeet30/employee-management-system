package com.project.ems.security.config;

import com.project.ems.security.entity.Role;
import com.project.ems.security.entity.RoleType;
import com.project.ems.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inserts default roles into the database
 * when the application starts.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        createRoleIfNotExists(
                RoleType.ADMIN,
                "System Administrator"
        );

        createRoleIfNotExists(
                RoleType.HR,
                "Human Resource"
        );

        createRoleIfNotExists(
                RoleType.EMPLOYEE,
                "Regular Employee"
        );
    }

    /**
     * Creates a role only if it doesn't already exist.
     */
    private void createRoleIfNotExists(
            RoleType roleType,
            String description) {

        if (!roleRepository.existsByName(roleType)) {

            Role role = Role.builder()
                    .name(roleType)
                    .description(description)
                    .build();

            roleRepository.save(role);
        }
    }
}