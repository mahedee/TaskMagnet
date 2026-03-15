package com.mahedee.backend.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahedee.backend.models.ERole;
import com.mahedee.backend.models.Role;
import com.mahedee.backend.models.User;
import com.mahedee.backend.payload.request.LoginRequest;
import com.mahedee.backend.payload.request.SignupRequest;
import com.mahedee.backend.payload.response.JwtResponse;
import com.mahedee.backend.payload.response.MessageResponse;
import com.mahedee.backend.repository.RoleRepository;
import com.mahedee.backend.repository.UserRepository;
import com.mahedee.backend.security.jwt.JwtUtils;
import com.mahedee.backend.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

/**
 * Authentication Controller for TaskMagnet Application
 * 
 * This REST controller handles all authentication-related operations including
 * user registration, login, and JWT token management. It provides secure endpoints
 * for user authentication and account creation with role-based access control.
 * 
 * Authentication Features:
 * - User Login: Validates credentials and issues JWT tokens
 * - User Registration: Creates new user accounts with role assignment
 * - Role-based Security: Supports multiple user roles (USER, MODERATOR, ADMIN)
 * - JWT Token Generation: Stateless authentication with secure tokens
 * - Input Validation: Comprehensive validation for all incoming requests
 * - CORS Support: Cross-origin resource sharing for frontend integration
 * 
 * Security Measures:
 * - Password encryption using BCrypt
 * - Duplicate username/email prevention
 * - Role-based access control (RBAC)
 * - JWT token-based stateless authentication
 * - Input sanitization and validation
 * 
 * API Endpoints:
 * - POST /api/auth/signin: User authentication and token generation
 * - POST /api/auth/signup: User registration with role assignment
 * 
 * @author TaskMagnet Security Team
 * @version 3.0.0
 * @since 1.0.0
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    /**
     * Spring Security Authentication Manager
     * Handles the core authentication logic by validating user credentials
     * against configured authentication providers
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * User Repository for database operations
     * Provides CRUD operations for user entities and custom queries
     * for username and email validation
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Role Repository for role management
     * Handles role-related database operations and role lookups
     * for user registration and role assignment
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Password Encoder for secure password hashing
     * Uses BCrypt algorithm to hash passwords before storing in database
     * Provides secure password verification during authentication
     */
    @Autowired
    private PasswordEncoder encoder;

    /**
     * JWT Utility for token operations
     * Handles JWT token generation, validation, and user information extraction
     * Central component for stateless authentication implementation
     */
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * User Authentication Endpoint
     * 
     * Authenticates user credentials and generates a JWT token for subsequent requests.
     * This endpoint validates the provided username and password against the database,
     * and returns a JWT token along with user information if authentication succeeds.
     * 
     * Authentication Flow:
     * 1. Validate input credentials format and required fields
     * 2. Create authentication token with username and password
     * 3. Delegate authentication to Spring Security's AuthenticationManager
     * 4. Generate JWT token using authenticated user information
     * 5. Extract user roles and authorities from authentication object
     * 6. Return JWT token and user details in response
     * 
     * Security Features:
     * - BCrypt password verification
     * - Authentication failure handling
     * - Role information inclusion in response
     * - JWT token generation with expiration
     * 
     * Response Format:
     * - Success: JwtResponse with token, user ID, username, email, and roles
     * - Failure: HTTP 401 Unauthorized with error message
     * 
     * @param loginRequest Login credentials containing username and password
     * @return ResponseEntity with JWT token and user information or error message
     * @throws BadCredentialsException if credentials are invalid
     * @throws AccountStatusException if account is disabled or locked
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Create authentication token from login credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()));

        // Set authentication in security context (for current request)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token for authenticated user
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // Extract user details from authentication object
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Convert user authorities to string list for response
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Return successful authentication response with JWT token and user details
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    /**
     * User Registration Endpoint
     * 
     * Creates a new user account with specified roles and credentials.
     * This endpoint validates the registration data, checks for existing users,
     * creates a new user with encrypted password, and assigns appropriate roles.
     * 
     * Registration Flow:
     * 1. Validate input data format and required fields
     * 2. Check for existing users with same username or email
     * 3. Create new user entity with encrypted password
     * 4. Process role assignment based on request or defaults
     * 5. Save user to database with assigned roles
     * 6. Return success confirmation
     * 
     * Role Assignment Logic:
     * - If no roles specified: Assign default USER role
     * - If "admin" specified: Assign ADMIN role
     * - If "mod" specified: Assign MODERATOR role
     * - If unknown role specified: Default to USER role
     * - Multiple roles can be assigned to a single user
     * 
     * Validation Rules:
     * - Username must be unique across the system
     * - Email must be unique across the system
     * - Password must meet complexity requirements (handled by validation)
     * - Role assignments must reference existing roles in database
     * 
     * Security Features:
     * - Password encryption before database storage
     * - Duplicate prevention for username and email
     * - Role validation against existing roles
     * - Input sanitization and validation
     * 
     * @param signupRequest Registration data including username, email, password, and roles
     * @return ResponseEntity with success message or error details
     * @throws DataIntegrityViolationException if database constraints are violated
     * @throws RuntimeException if required roles are not found in database
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        // Check if username is already taken
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if email is already registered
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        // Create new user entity with encrypted password
        User user = new User(signupRequest.getUsername(), 
                           signupRequest.getEmail(),
                           encoder.encode(signupRequest.getPassword()));
        
        // Process role assignment
        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        // Assign roles based on request or default to USER role
        if (strRoles == null) {
            // Default role assignment: USER
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            // Process each requested role
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        // Assign ADMIN role with full system access
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                        
                    case "mod":
                        // Assign MODERATOR role with limited administrative access
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                        
                    default:
                        // Default to USER role for unrecognized role strings
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        // Assign roles to user and save to database
        user.setRoles(roles);
        userRepository.save(user);

        // Return success response
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
