package com.project.ems.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security filter responsible for authenticating requests
 * that contain a JWT access token.
 *
 * This filter runs once for every incoming HTTP request.
 *
 * Its responsibility is to:
 * 1. Read the Authorization header.
 * 2. Extract the Bearer token.
 * 3. Extract the username from the token.
 * 4. Load the user from the database.
 * 5. Validate the token.
 * 6. Store authentication in Spring Security's SecurityContext.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * Prefix expected before the JWT token.
     *
     * Example:
     * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Service used to read and validate JWT tokens.
     */
    private final JwtService jwtService;

    /**
     * Service used to load user details from the database.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Executes once for every incoming HTTP request.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param filterChain remaining Spring Security filter chain
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Read the complete Authorization header.
        String authorizationHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        /*
         * If the header is missing or does not start with "Bearer ",
         * this filter cannot authenticate the request.
         *
         * Public endpoints may continue normally.
         * Protected endpoints will later return 401 through Spring Security.
         */
        if (!StringUtils.hasText(authorizationHeader)
                || !authorizationHeader.startsWith(BEARER_PREFIX)) {

            filterChain.doFilter(request, response);
            return;
        }

        // Remove the "Bearer " prefix and keep only the JWT value.
        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        try {
            // Extract the username stored as the JWT subject.
            String username = jwtService.extractUsername(token);

            /*
             * Authenticate only when:
             * 1. A username was successfully extracted.
             * 2. The SecurityContext does not already contain authentication.
             *
             * This prevents overwriting authentication created by another filter.
             */
            if (StringUtils.hasText(username)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load current user information from the database.
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                // Validate token ownership, signature and expiration.
                if (jwtService.isTokenValid(token, userDetails.getUsername())) {

                    /*
                     * Create an authenticated Spring Security token.
                     *
                     * Principal:
                     * UserDetails object
                     *
                     * Credentials:
                     * null because the password has already been authenticated
                     *
                     * Authorities:
                     * ROLE_ADMIN, ROLE_HR or ROLE_EMPLOYEE
                     */
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * Store request-specific information such as
                     * IP address and session ID.
                     */
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /*
                     * Save authentication in the current SecurityContext.
                     *
                     * After this line, Spring Security treats the request
                     * as authenticated.
                     */
                    SecurityContextHolder.getContext()
                            .setAuthentication(authenticationToken);

                    logger.info(
                            "JWT authenticated user: {}, authorities: {}",
                            userDetails.getUsername(),
                            userDetails.getAuthorities()
                    );
                }
            }

        } catch (JwtException exception) {

            /*
             * Handles invalid signature, expired token,
             * malformed token and other JWT parsing errors.
             *
             * We do not expose detailed token errors to the client.
             */
            logger.warn(
                    "JWT authentication failed for request {}: {}",
                    request.getRequestURI(),
                    exception.getMessage()
            );

            // Ensure no previous authentication remains in the context.
            SecurityContextHolder.clearContext();

        } catch (IllegalArgumentException exception) {

            // Handles an empty or otherwise invalid token argument.
            logger.warn(
                    "Invalid JWT token supplied for request {}",
                    request.getRequestURI()
            );

            SecurityContextHolder.clearContext();
        }

        /*
         * Continue to the remaining Spring Security filters.
         *
         * If authentication was successfully stored, protected APIs may proceed.
         * Otherwise, SecurityConfig will deny protected requests.
         */
        filterChain.doFilter(request, response);
    }
}