package com.mahedee.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mahedee.backend.security.jwt.AuthEntryPointJwt;
import com.mahedee.backend.security.jwt.AuthTokenFilter;
import com.mahedee.backend.security.services.UserDetailsServiceImpl;

/**
 * Spring Security Configuration for TaskMagnet Application
 * 
 * This configuration class sets up the security framework for the TaskMagnet application,
 * implementing JWT-based authentication and role-based authorization.
 * 
 * Security Features:
 * - JWT Token Authentication: Stateless authentication using JSON Web Tokens
 * - BCrypt Password Encoding: Secure password hashing with configurable strength
 * - Role-Based Access Control: Method-level and URL-level security
 * - CORS Support: Cross-origin resource sharing for frontend integration
 * - API Documentation Access: Public access to Swagger UI and OpenAPI docs
 * 
 * Security Architecture:
 * 1. Authentication Filter: Intercepts requests and validates JWT tokens
 * 2. Authentication Provider: Validates user credentials against database
 * 3. User Details Service: Loads user information for authentication
 * 4. Authorization: Enforces access control based on user roles and permissions
 * 
 * @author TaskMagnet Security Team
 * @version 3.0.0
 * @since 1.0.0
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {
    
    /**
     * Custom user details service implementation for loading user-specific data
     * Used by the authentication provider to validate user credentials
     */
    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    /**
     * JWT authentication entry point for handling unauthorized access attempts
     * Returns a 401 Unauthorized response when authentication fails
     */
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    /**
     * Creates and configures the JWT authentication token filter
     * 
     * This filter intercepts incoming HTTP requests and extracts JWT tokens
     * from the Authorization header. It validates the token and sets the
     * authentication context for the current request.
     * 
     * @return Configured AuthTokenFilter instance
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Password encoder bean using BCrypt hashing algorithm
     * 
     * BCrypt is a robust password hashing function designed to be slow and
     * computationally expensive to prevent brute-force attacks. It includes
     * built-in salt generation and is resistant to rainbow table attacks.
     * 
     * Security Features:
     * - Adaptive hashing: Cost factor can be increased as hardware improves
     * - Built-in salt: Each password gets a unique salt value
     * - Time-tested: Industry standard for password hashing
     * 
     * @return BCryptPasswordEncoder instance with default strength (10 rounds)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Using BCrypt with default strength (10 rounds)
        // This provides a good balance between security and performance
        return new BCryptPasswordEncoder();
    }

    /**
     * Database Authentication Provider Configuration
     * 
     * Creates and configures a DaoAuthenticationProvider that authenticates users
     * against the database. This provider uses the custom UserDetailsService to
     * load user information and the BCrypt encoder to verify passwords.
     * 
     * Authentication Flow:
     * 1. Receives username/password from login request
     * 2. Uses UserDetailsService to load user from database
     * 3. Uses PasswordEncoder to verify the provided password
     * 4. Returns authentication result (success/failure)
     * 
     * @return Configured DaoAuthenticationProvider instance
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // Set the custom user details service for loading user data
        authProvider.setUserDetailsService(userDetailsServiceImpl);
        
        // Set the password encoder for password verification
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    /**
     * Authentication Manager Bean Configuration
     * 
     * The AuthenticationManager is the main interface for authenticating users.
     * It delegates the actual authentication to configured AuthenticationProviders.
     * This bean is required for programmatic authentication in controllers.
     * 
     * @param authConfig Authentication configuration containing providers and settings
     * @return AuthenticationManager instance
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * HTTP Security Filter Chain Configuration
     * 
     * This is the core security configuration that defines how HTTP requests
     * are secured. It configures authentication, authorization, session management,
     * and exception handling for the entire application.
     * 
     * Security Configuration:
     * - CSRF Protection: Disabled for stateless JWT authentication
     * - Session Management: Stateless (no server-side sessions)
     * - Exception Handling: Custom entry point for authentication failures
     * - Authorization Rules: Role-based access control for different endpoints
     * - Filter Chain: JWT authentication filter added before username/password filter
     * 
     * Public Endpoints (No Authentication Required):
     * - /api/auth/** : Authentication endpoints (login, register, refresh)
     * - /api/test/** : Test endpoints for development
     * - /swagger-ui/** : Swagger UI interface
     * - /v3/api-docs/** : OpenAPI documentation
     * 
     * Protected Endpoints (Authentication Required):
     * - All other endpoints require valid JWT token
     * 
     * @param http HttpSecurity configuration object
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection as we're using stateless JWT authentication
            .csrf(csrf -> csrf.disable())
            
            // Configure exception handling for authentication failures
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(unauthorizedHandler))
            
            // Configure session management as stateless (no server-side sessions)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization rules for different URL patterns
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers("/api/auth/**").permitAll()  // Authentication endpoints
                .requestMatchers("/api/test/**").permitAll()  // Test endpoints
                
                // API Documentation endpoints - public access for development
                .requestMatchers("/v3/api-docs/**",
                               "/swagger-ui/**",
                               "/swagger-ui.html", 
                               "/api-docs/**",
                               "/swagger-resources/**",
                               "/webjars/**").permitAll()
                
                // All other endpoints require authentication
                .anyRequest().authenticated());

        // Set the custom authentication provider
        http.authenticationProvider(authenticationProvider());
        
        // Add JWT authentication filter before the default username/password filter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
