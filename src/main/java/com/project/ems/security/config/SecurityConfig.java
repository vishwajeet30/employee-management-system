package com.project.ems.security.config;

import com.project.ems.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main Spring Security configuration.
 *
 * This class defines:
 * - Public endpoints
 * - Protected endpoints
 * - Role-based authorization
 * - Stateless session handling
 * - JWT filter placement
 * - CSRF configuration
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Custom filter that reads and validates JWT tokens.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the Spring Security filter chain.
     *
     * @param http HttpSecurity configuration object
     * @return configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                /*
                 * CSRF protection is normally important for browser-based
                 * session applications.
                 *
                 * This application uses stateless JWT authentication,
                 * so CSRF protection is disabled for the REST API.
                 */
                .csrf(csrf -> csrf.disable())

                /*
                 * Disable server-side HTTP sessions.
                 *
                 * Spring Security will not store authentication
                 * between requests.
                 *
                 * Every protected request must send its JWT token.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * Define authorization rules.
                 *
                 * Rules are checked from top to bottom.
                 * More specific rules should appear before general rules.
                 */
                .authorizeHttpRequests(authorize -> authorize

                        /*
                         * The compiled React frontend (served as static
                         * resources by Spring Boot) and the deployment
                         * health check must remain public.
                         *
                         * Without this rule, "/" falls through to the
                         * "anyRequest().authenticated()" rule below and
                         * Spring Security returns 403 before the request
                         * ever reaches WelcomePageHandlerMapping.
                         */
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/assets/**",
                                "/static/**",
                                "/*.js",
                                "/*.css",
                                "/*.svg",
                                "/*.png",
                                "/error"
                        )
                        .permitAll()

                        /*
                         * Registration and login must remain public.
                         */
                        .requestMatchers("/api/v1/auth/**")
                        .permitAll()

                        /*
                         * Swagger and OpenAPI documentation must remain public.
                         */
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        )
                        .permitAll()

                        /*
                         * ADMIN and HR can create employees.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/employees",
                                "/api/v1/employees/"
                        )
                        .hasAnyRole("ADMIN", "HR")

                        /*
                         * ADMIN and HR can update employee records.
                         */
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/employees/**"
                        )
                        .hasAnyRole("ADMIN", "HR")

                        /*
                         * Only ADMIN can delete employees.
                         */
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/employees/**"
                        )
                        .hasRole("ADMIN")

                        /*
                         * Any authenticated user can view and search employees.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/employees",
                                "/api/v1/employees/**"
                        )
                        .authenticated()

                        /*
                         * All other endpoints require authentication.
                         */
                        .anyRequest()
                        .authenticated()
                )

                /*
                 * Disable the default HTML login form.
                 *
                 * Login is handled through:
                 * POST /api/v1/auth/login
                 */
                .formLogin(form -> form.disable())

                /*
                 * Disable HTTP Basic authentication.
                 *
                 * Requests should authenticate using JWT Bearer tokens.
                 */
                .httpBasic(httpBasic -> httpBasic.disable())

                /*
                 * Add our JWT filter before Spring Security's standard
                 * username/password authentication filter.
                 *
                 * This gives the JWT filter an opportunity to authenticate
                 * the request before authorization rules are evaluated.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
