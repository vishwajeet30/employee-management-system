package com.project.ems.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Service responsible for generating and validating JWT tokens.
 */
@Service
public class JwtService {

    /**
     * Secret key used to sign JWT tokens.
     *
     * IMPORTANT:
     * In production, keep this in application.yml or environment variables,
     * not hardcoded.
     */
    private static final String SECRET_KEY =
            "my-super-secret-key-my-super-secret-key-123456";

    /**
     * Token validity time.
     * Here: 24 hours.
     */
    private static final long EXPIRATION_TIME =
            1000 * 60 * 60 * 24;

    /**
     * Generates JWT token using username and role.
     *
     * @param username logged-in username
     * @param role user role
     * @return JWT token
     */
    public String generateToken(String username, String role) {

        return Jwts.builder()
                // Username is stored as subject
                .subject(username)

                // Custom claim for role
                .claim("role", role)

                // Token creation time
                .issuedAt(new Date())

                // Token expiry time
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))

                // Sign token using secret key
                .signWith(getSigningKey())

                .compact();
    }

    /**
     * Extracts username from JWT token.
     */
    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts user role from JWT token.
     */
    public String extractRole(String token) {

        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Checks whether token is valid for given username.
     */
    public boolean isTokenValid(String token, String username) {

        String extractedUsername = extractUsername(token);

        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }

    /**
     * Checks whether token is expired.
     */
    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /**
     * Extracts all claims from JWT token.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Converts secret string into signing key.
     */
    private Key getSigningKey() {

        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}