package com.project.ems.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for creating, reading and validating JWT tokens.
 */
@Service
public class JwtService {

    /**
     * Base64-encoded secret key loaded from application configuration.
     */
    private final String secretKey;

    /**
     * Access-token validity duration in milliseconds.
     */
    private final long accessTokenExpiration;

    /**
     * Constructor injection for JWT configuration values.
     *
     * @param secretKey Base64-encoded signing secret
     * @param accessTokenExpiration token lifetime in milliseconds
     */
    public JwtService(
            @Value("${application.security.jwt.secret-key}")
            String secretKey,

            @Value("${application.security.jwt.access-token-expiration}")
            long accessTokenExpiration) {

        this.secretKey = secretKey;
        this.accessTokenExpiration = accessTokenExpiration;
    }

    /**
     * Generates a JWT containing username and role.
     *
     * @param username authenticated username
     * @param role authenticated user's role
     * @return signed JWT access token
     */
    public String generateToken(String username, String role) {

        Map<String, Object> claims = Map.of(
                "role", role
        );

        return buildToken(
                claims,
                username,
                accessTokenExpiration
        );
    }

    /**
     * Creates and signs a JWT token.
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            String username,
            long expiration) {

        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the username stored in the token subject.
     *
     * @param token JWT token
     * @return username
     */
    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    /**
     * Extracts the role claim stored in the JWT.
     *
     * @param token JWT token
     * @return role name
     */
    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }

    /**
     * Extracts one claim using the supplied resolver function.
     */
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Validates whether the token belongs to the supplied username
     * and has not expired.
     *
     * @param token JWT token
     * @param username expected username
     * @return true when valid
     */
    public boolean isTokenValid(
            String token,
            String username) {

        String extractedUsername = extractUsername(token);

        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }

    /**
     * Alternative validation method using UserDetails.
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        return isTokenValid(
                token,
                userDetails.getUsername()
        );
    }

    /**
     * Checks whether the token expiry date is before the current time.
     */
    private boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    /**
     * Extracts the token expiration timestamp.
     */
    private Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    /**
     * Parses and verifies the signed JWT.
     *
     * Signature verification happens automatically using
     * the configured signing key.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Converts the Base64 secret into a cryptographic signing key.
     */
    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}