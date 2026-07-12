package com.project.ems.security.config;

import com.project.ems.security.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class responsible for defining
 * Spring Security-related beans.
 *
 * The beans declared here are managed by Spring's IoC container
 * and can be injected into other classes using constructor injection.
 */
@Configuration
public class SecurityBeansConfig {

    /**
     * Creates the PasswordEncoder used for hashing and verifying passwords.
     *
     * BCrypt generates a one-way password hash and automatically
     * manages the password salt.
     *
     * @return BCrypt-based password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates the UserDetailsService used by Spring Security
     * during username/password authentication.
     *
     * It loads the application's User entity from MySQL and converts it
     * into Spring Security's UserDetails representation.
     *
     * @param userRepository repository used to find users
     * @return database-backed UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService(
            UserRepository userRepository) {

        return username -> userRepository.findByUsername(username)

                // Convert our database User entity into Spring's UserDetails object
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())

                        // Password is already BCrypt encoded in the database
                        .password(user.getPassword())

                        /*
                         * disabled() expects true when the user should be disabled.
                         * Our entity stores enabled, so the value must be reversed.
                         */
                        .disabled(!user.getEnabled())

                        /*
                         * accountLocked() expects true when the account is locked.
                         * Our entity stores accountNonLocked, so this is also reversed.
                         */
                        .accountLocked(!user.getAccountNonLocked())

                        /*
                         * hasRole("ADMIN") checks for the authority ROLE_ADMIN.
                         * Therefore, the ROLE_ prefix is added here.
                         */
                        .authorities(
                                "ROLE_" + user.getRole().getName().name()
                        )

                        .build())

                // Executed when no matching username exists in the database
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username
                ));
    }

    /**
     * Exposes Spring Security's AuthenticationManager as a bean.
     *
     * AuthenticationManager verifies the username and password
     * supplied during login.
     *
     * @param authenticationConfiguration Spring Security configuration
     * @return configured AuthenticationManager
     * @throws Exception if the manager cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }
}