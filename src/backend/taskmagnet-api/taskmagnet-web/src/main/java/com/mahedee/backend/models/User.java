package com.mahedee.backend.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User Entity for TaskMagnet Application
 * 
 * This JPA entity represents a user in the TaskMagnet system with authentication
 * and authorization capabilities. It extends BaseEntity to inherit common fields
 * like ID, creation timestamp, and modification tracking.
 * 
 * Entity Features:
 * - Unique username and email constraints
 * - Password encryption support
 * - Role-based access control through many-to-many relationship
 * - Comprehensive input validation
 * - Audit trail inheritance from BaseEntity
 * 
 * Security Features:
 * - Password field for encrypted storage
 * - Role-based authorization support
 * - Email validation for communication
 * - Username uniqueness for identity
 * 
 * Database Mapping:
 * - Table: users
 * - Primary Key: id (inherited from BaseEntity)
 * - Unique Constraints: username, email
 * - Join Table: user_roles (for role relationships)
 * 
 * Validation Rules:
 * - Username: Required, max 50 characters, unique
 * - Email: Required, max 250 characters, valid email format, unique
 * - Password: Required, max 250 characters (should be encrypted)
 * - Roles: Optional set of assigned roles
 * 
 * @author TaskMagnet Development Team
 * @version 3.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
public class User extends BaseEntity<Long> {

    /**
     * Unique username for user identification and login
     * 
     * The username serves as the primary identifier for user authentication.
     * It must be unique across the system and is used for login purposes.
     * 
     * Validation Rules:
     * - Cannot be null or blank
     * - Maximum length of 50 characters
     * - Must be unique across all users
     * - Recommended format: alphanumeric with optional underscores/hyphens
     */
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;
   
    /**
     * User's email address for communication and identification
     * 
     * The email address is used for:
     * - User communication and notifications
     * - Account recovery and password reset
     * - Alternative login method (if implemented)
     * - System-generated notifications
     * 
     * Validation Rules:
     * - Cannot be null or blank
     * - Maximum length of 250 characters
     * - Must be a valid email format
     * - Must be unique across all users
     */
    @NotBlank(message = "Email is required")
    @Size(max = 250, message = "Email must not exceed 250 characters")
    @Email(message = "Email must be a valid email address")
    private String email;

    /**
     * Encrypted password for user authentication
     * 
     * This field stores the user's password in encrypted form using BCrypt
     * hashing algorithm. The original password is never stored in plain text.
     * 
     * Security Features:
     * - BCrypt encryption with salt
     * - Configurable encryption strength
     * - Resistant to rainbow table attacks
     * - One-way hashing (cannot be decrypted)
     * 
     * Validation Rules:
     * - Cannot be null or blank
     * - Maximum length of 250 characters (encrypted form)
     * - Original password should meet complexity requirements
     */
    @NotBlank(message = "Password is required")
    @Size(max = 250, message = "Encrypted password must not exceed 250 characters")
    private String password;

    /**
     * Set of roles assigned to this user for authorization
     * 
     * Implements Role-Based Access Control (RBAC) through a many-to-many
     * relationship with the Role entity. Each user can have multiple roles,
     * and each role can be assigned to multiple users.
     * 
     * Relationship Features:
     * - Many-to-Many: Users ↔ Roles
     * - Lazy Loading: Roles loaded on-demand for performance
     * - Join Table: user_roles with foreign keys
     * - Cascade Operations: Handled at application level
     * 
     * Join Table Structure:
     * - Table Name: user_roles
     * - user_id: Foreign key to users table
     * - role_id: Foreign key to roles table
     * 
     * Default Initialization:
     * - Initialized as empty HashSet to prevent null pointer exceptions
     * - Allows immediate role additions without null checks
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", 
               joinColumns = @JoinColumn(name = "user_id"), 
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Default constructor for JPA and serialization frameworks
     * 
     * This constructor is required by JPA for entity instantiation
     * during database operations and object-relational mapping.
     */
    public User() {
        super();
    }

    /**
     * Constructor for creating new user with basic information
     * 
     * Creates a new user instance with essential fields. The password
     * should be provided in encrypted form from the service layer.
     * 
     * @param username Unique username for the user
     * @param email Valid email address for the user
     * @param password Encrypted password for authentication
     */
    public User(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters with comprehensive documentation

    /**
     * Gets the username of the user
     * @return The unique username used for authentication
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user
     * @param username The unique username for authentication
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email address of the user
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email address of the user
     * @param email Valid email address for the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the encrypted password of the user
     * @return The encrypted password (never plain text)
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the encrypted password of the user
     * @param password The encrypted password (should never be plain text)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the set of roles assigned to this user
     * @return Set of roles for authorization and access control
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the roles for this user
     * @param roles Set of roles to assign to the user
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * Adds a single role to the user's role set
     * 
     * Convenience method for adding individual roles without
     * replacing the entire role set.
     * 
     * @param role The role to add to the user
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Removes a role from the user's role set
     * 
     * Convenience method for removing individual roles without
     * replacing the entire role set.
     * 
     * @param role The role to remove from the user
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Checks if the user has a specific role
     * 
     * @param role The role to check for
     * @return true if the user has the specified role
     */
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }
}
