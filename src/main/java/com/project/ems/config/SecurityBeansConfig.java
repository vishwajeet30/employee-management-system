package com.project.ems.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class responsible for defining
 * Spring-managed security beans.
 *
 * Beans defined here can be injected anywhere
 * in the application using constructor injection.
 */
@Configuration
public class SecurityBeansConfig {

    /**
     * Creates a PasswordEncoder bean.
     *
     * BCrypt is the recommended password hashing
     * algorithm for Spring Security.
     *
     * Spring creates this bean once and reuses it
     * throughout the application.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}