package com.mahedee.backend.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.mahedee.backend.security.services.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * JWT (JSON Web Token) Utility Class for TaskMagnet Application
 * 
 * This utility class handles all JWT token operations including generation, validation,
 * and extraction of user information from tokens. It provides secure, stateless
 * authentication for the TaskMagnet API.
 * 
 * JWT Security Features:
 * - HMAC-SHA256 Signature: Ensures token integrity and authenticity
 * - Configurable Expiration: Tokens have a limited lifespan for security
 * - Claims-based Authentication: Username stored in token subject claim
 * - Comprehensive Validation: Multiple security checks on token parsing
 * 
 * Token Structure:
 * - Header: Algorithm and token type (HS256, JWT)
 * - Payload: Claims including subject (username), issued at, and expiration
 * - Signature: HMAC-SHA256 signature using secret key
 * 
 * Security Considerations:
 * - Secret key should be at least 256 bits and stored securely
 * - Tokens should be transmitted over HTTPS only
 * - Short expiration times reduce risk of token compromise
 * - Proper validation prevents token tampering and forgery
 * 
 * @author TaskMagnet Security Team
 * @version 3.0.0
 * @since 1.0.0
 */
@Component
public class JwtUtils {

    /**
     * Logger instance for JWT operations and security events
     * Used to log token generation, validation attempts, and security violations
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * JWT Secret Key for token signing and verification
     * 
     * This secret is used to sign JWT tokens with HMAC-SHA256 algorithm.
     * The key must be:
     * - At least 256 bits (32 bytes) long for HS256
     * - Base64 encoded for configuration storage
     * - Kept secure and not exposed in logs or error messages
     * - Rotated periodically for enhanced security
     */
    @Value("${mahedee.app.jwtSecret}")
    private String jwtSecret;

    /**
     * JWT Token Expiration Time in milliseconds
     * 
     * Configures how long JWT tokens remain valid before expiring.
     * Shorter expiration times improve security but may affect user experience.
     * 
     * Common values:
     * - 900000 (15 minutes) - High security applications
     * - 3600000 (1 hour) - Balanced security and usability
     * - 86400000 (24 hours) - User-friendly but less secure
     */
    @Value("${mahedee.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Generates a JWT token for authenticated user
     * 
     * Creates a new JWT token containing the user's identity and authentication
     * information. The token includes:
     * - Subject: Username of the authenticated user
     * - Issued At: Timestamp when token was created
     * - Expiration: Timestamp when token expires
     * - Signature: HMAC-SHA256 signature for integrity verification
     * 
     * Token Generation Process:
     * 1. Extract username from authentication principal
     * 2. Set token subject to username
     * 3. Set issued at timestamp to current time
     * 4. Calculate expiration time based on configuration
     * 5. Sign token with secret key using HS256 algorithm
     * 6. Compact token to standard JWT format
     * 
     * @param authentication Spring Security Authentication object containing user details
     * @return Compact JWT token string ready for client transmission
     * @throws ClassCastException if authentication principal is not UserDetailsImpl
     */
    public String generateJwtToken(Authentication authentication) {
        // Extract user details from authentication object
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        // Build and return JWT token with user claims and signature
        return Jwts.builder()
                .subject(userPrincipal.getUsername())                        // Set username as subject
                .issuedAt(new Date())                                        // Set current timestamp
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Set expiration
                .signWith(key())                                             // Sign with HMAC-SHA256
                .compact();                                                  // Generate compact token string
    }

    /**
     * Generates HMAC-SHA256 signing key from configured secret
     * 
     * Creates a cryptographic key for JWT token signing and verification.
     * The secret is decoded from Base64 format and used to create an HMAC key
     * suitable for the HS256 algorithm.
     * 
     * Key Generation Process:
     * 1. Decode Base64-encoded secret from configuration
     * 2. Generate HMAC-SHA key from decoded bytes
     * 3. Return key instance for JWT operations
     * 
     * Security Notes:
     * - Key derivation is performed on each use for memory security
     * - Original secret remains in Base64 format in configuration
     * - Key instance is not cached to prevent memory exposure
     * 
     * @return HMAC-SHA256 Key instance for JWT signing and verification
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Extracts username from JWT token
     * 
     * Parses a JWT token and extracts the username from the subject claim.
     * This method assumes the token is valid and has been previously verified.
     * 
     * Token Parsing Process:
     * 1. Create JWT parser with signing key verification
     * 2. Parse the token and extract claims
     * 3. Return the subject claim (username)
     * 
     * Security Notes:
     * - Token signature is verified during parsing
     * - Invalid tokens will throw exceptions
     * - This method should only be called after token validation
     * 
     * @param token Valid JWT token string
     * @return Username extracted from token subject claim
     * @throws JwtException if token is invalid, expired, or malformed
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())                                  // Set verification key
                .build()                                            // Build parser
                .parseSignedClaims(token)                          // Parse and verify token
                .getPayload()                                      // Get claims payload
                .getSubject();                                     // Extract subject (username)
    }

    /**
     * Validates JWT token integrity and authenticity
     * 
     * Performs comprehensive validation of a JWT token including:
     * - Signature verification using the secret key
     * - Token structure and format validation
     * - Expiration time checking
     * - Claims integrity verification
     * 
     * Validation Process:
     * 1. Parse token with signing key verification
     * 2. Check token structure and format
     * 3. Verify signature authenticity
     * 4. Validate expiration time
     * 5. Return validation result
     * 
     * Handled Security Exceptions:
     * - MalformedJwtException: Invalid token structure or format
     * - ExpiredJwtException: Token has passed expiration time
     * - UnsupportedJwtException: Token uses unsupported features
     * - IllegalArgumentException: Empty or null token claims
     * 
     * Security Logging:
     * - All validation failures are logged for security monitoring
     * - Logs include exception type and message for debugging
     * - Sensitive token content is not logged for security
     * 
     * @param authToken JWT token string to validate
     * @return true if token is valid and can be trusted, false otherwise
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Attempt to parse and verify the token
            Jwts.parser()
                .verifyWith(key())                                  // Set verification key
                .build()                                            // Build parser instance
                .parseSignedClaims(authToken);                     // Parse and validate token
            
            return true; // Token is valid
            
        } catch (MalformedJwtException e) {
            // Token structure is invalid or corrupted
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // Token has exceeded its expiration time
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // Token uses features not supported by this implementation
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // Token claims are empty or null
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false; // Token validation failed
    }
}
