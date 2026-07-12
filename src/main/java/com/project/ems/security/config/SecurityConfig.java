package com.project.ems.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Main Spring Security configuration class.
 *
 * This class defines:
 * - Which APIs are public
 * - Which APIs need authentication
 * - Role-based access rules
 * - Stateless session management for JWT
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Configures the Spring Security filter chain.
     *
     * Since we are building REST APIs with JWT:
     * - CSRF is disabled
     * - Sessions are stateless
     * - Auth endpoints are public
     * - Swagger endpoints are public
     * - Employee APIs are protected based on role
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        return http
                // Disable CSRF because JWT-based REST APIs do not use server-side sessions
                .csrf(csrf -> csrf.disable())

                // Make the application stateless because JWT will be sent with every request
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configure API authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Public authentication APIs: register/login
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Public Swagger/OpenAPI endpoints
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()

                        // Only ADMIN and HR can create employees
                        .requestMatchers(HttpMethod.POST, "/api/v1/employees/**")
                        .hasAnyRole("ADMIN", "HR")

                        // Only ADMIN and HR can update employees
                        .requestMatchers(HttpMethod.PUT, "/api/v1/employees/**")
                        .hasAnyRole("ADMIN", "HR")

                        // Only ADMIN can delete employees
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/employees/**")
                        .hasRole("ADMIN")

                        // ADMIN, HR, and EMPLOYEE can view employees
                        .requestMatchers(HttpMethod.GET, "/api/v1/employees/**")
                        .hasAnyRole("ADMIN", "HR", "EMPLOYEE")

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )

                .build();
    }
}