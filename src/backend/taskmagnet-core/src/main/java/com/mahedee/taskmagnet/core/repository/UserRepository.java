package com.mahedee.taskmagnet.core.repository;

import com.mahedee.taskmagnet.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by email verification token
     * @param token the verification token
     * @return Optional containing the user if found
     */
    Optional<User> findByEmailVerificationToken(String token);
    
    /**
     * Find user by password reset token
     * @param token the password reset token
     * @return Optional containing the user if found
     */
    Optional<User> findByPasswordResetToken(String token);
    
    /**
     * Check if username exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all active users
     * @return List of active users
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Find users by department
     * @param department the department name
     * @return List of users in the department
     */
    List<User> findByDepartmentAndIsActiveTrue(String department);
    
    /**
     * Find users with failed login attempts
     * @param attempts minimum number of failed attempts
     * @return List of users with failed login attempts
     */
    @Query("SELECT u FROM User u WHERE u.failedLoginAttempts >= :attempts AND u.isActive = true")
    List<User> findUsersWithFailedLoginAttempts(@Param("attempts") Integer attempts);
    
    /**
     * Find locked users
     * @return List of currently locked users
     */
    @Query("SELECT u FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > CURRENT_TIMESTAMP")
    List<User> findLockedUsers();
    
    /**
     * Find users who haven't logged in since a specific date
     * @param since the date to check against
     * @return List of inactive users
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginDate IS NULL OR u.lastLoginDate < :since")
    List<User> findInactiveUsersSince(@Param("since") LocalDateTime since);
    
    /**
     * Find users with unverified emails
     * @return List of users with unverified emails
     */
    List<User> findByIsEmailVerifiedFalseAndIsActiveTrue();
    
    /**
     * Find users by partial name match (first name or last name)
     * @param name the name to search for
     * @return List of matching users
     */
    @Query("SELECT u FROM User u WHERE (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND u.isActive = true")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Count active users
     * @return number of active users
     */
    long countByIsActiveTrue();
    
    /**
     * Count users by department
     * @param department the department name
     * @return number of users in the department
     */
    long countByDepartmentAndIsActiveTrue(String department);
}
