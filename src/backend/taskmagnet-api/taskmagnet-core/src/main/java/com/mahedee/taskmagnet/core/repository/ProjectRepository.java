package com.mahedee.taskmagnet.core.repository;

import com.mahedee.taskmagnet.core.entity.Project;
import com.mahedee.taskmagnet.core.entity.User;
import com.mahedee.taskmagnet.core.entity.Category;
import com.mahedee.taskmagnet.core.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Project entity operations.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    /**
     * Find project by code
     * @param code the project code
     * @return Optional containing the project if found
     */
    Optional<Project> findByCodeAndIsActiveTrue(String code);
    
    /**
     * Find projects by owner
     * @param owner the project owner
     * @return List of projects owned by the user
     */
    List<Project> findByOwnerAndIsActiveTrueOrderByCreatedDateDesc(User owner);
    
    /**
     * Find projects by status
     * @param status the project status
     * @return List of projects with the specified status
     */
    List<Project> findByStatusAndIsActiveTrue(ProjectStatus status);
    
    /**
     * Find projects by category
     * @param category the project category
     * @return List of projects in the category
     */
    List<Project> findByCategoryAndIsActiveTrueOrderByNameAsc(Category category);
    
    /**
     * Find active projects
     * @return List of active projects
     */
    List<Project> findByStatusActiveAndIsActiveTrue();
    
    /**
     * Find projects where user is a member
     * @param user the user
     * @return List of projects where the user is a member
     */
    @Query("SELECT p FROM Project p JOIN p.members m WHERE m = :user AND p.isActive = true ORDER BY p.name ASC")
    List<Project> findProjectsByMember(@Param("user") User user);
    
    /**
     * Find projects where user is owner or member
     * @param user the user
     * @return List of projects accessible to the user
     */
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.members m WHERE (p.owner = :user OR m = :user) AND p.isActive = true ORDER BY p.name ASC")
    List<Project> findProjectsAccessibleToUser(@Param("user") User user);
    
    /**
     * Find overdue projects
     * @param currentDate the current date
     * @return List of overdue projects
     */
    @Query("SELECT p FROM Project p WHERE p.targetCompletionDate < :currentDate AND p.status NOT IN ('COMPLETED', 'CANCELLED', 'ARCHIVED') AND p.isActive = true")
    List<Project> findOverdueProjects(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find projects due soon (within specified days)
     * @param fromDate the start date
     * @param toDate the end date
     * @return List of projects due within the date range
     */
    @Query("SELECT p FROM Project p WHERE p.targetCompletionDate BETWEEN :fromDate AND :toDate AND p.status NOT IN ('COMPLETED', 'CANCELLED', 'ARCHIVED') AND p.isActive = true")
    List<Project> findProjectsDueSoon(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
    
    /**
     * Find projects by name containing text (case-insensitive)
     * @param name the text to search for in project name
     * @return List of matching projects
     */
    @Query("SELECT p FROM Project p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.isActive = true")
    List<Project> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find projects with low progress (below specified percentage)
     * @param progressThreshold the progress threshold
     * @return List of projects with low progress
     */
    @Query("SELECT p FROM Project p WHERE p.progressPercentage < :threshold AND p.status = 'ACTIVE' AND p.isActive = true")
    List<Project> findProjectsWithLowProgress(@Param("threshold") Integer progressThreshold);
    
    /**
     * Find completed projects
     * @return List of completed projects
     */
    List<Project> findByStatusCompletedAndIsActiveTrue();
    
    /**
     * Find projects by date range
     * @param startDate the start date
     * @param endDate the end date
     * @return List of projects created within the date range
     */
    @Query("SELECT p FROM Project p WHERE p.startDate BETWEEN :startDate AND :endDate AND p.isActive = true")
    List<Project> findProjectsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Check if project code exists
     * @param code the project code
     * @return true if code exists, false otherwise
     */
    boolean existsByCodeAndIsActiveTrue(String code);
    
    /**
     * Count projects by owner
     * @param owner the project owner
     * @return number of projects owned by the user
     */
    long countByOwnerAndIsActiveTrue(User owner);
    
    /**
     * Count projects by status
     * @param status the project status
     * @return number of projects with the specified status
     */
    long countByStatusAndIsActiveTrue(ProjectStatus status);
    
    /**
     * Count overdue projects
     * @param currentDate the current date
     * @return number of overdue projects
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE p.targetCompletionDate < :currentDate AND p.status NOT IN ('COMPLETED', 'CANCELLED', 'ARCHIVED') AND p.isActive = true")
    long countOverdueProjects(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Get project statistics for dashboard
     * @return Map containing project statistics
     */
    @Query("SELECT new map(" +
           "SUM(CASE WHEN p.status = 'PLANNING' THEN 1 ELSE 0 END) as planning, " +
           "SUM(CASE WHEN p.status = 'ACTIVE' THEN 1 ELSE 0 END) as active, " +
           "SUM(CASE WHEN p.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
           "SUM(CASE WHEN p.targetCompletionDate < CURRENT_DATE AND p.status NOT IN ('COMPLETED', 'CANCELLED', 'ARCHIVED') THEN 1 ELSE 0 END) as overdue" +
           ") FROM Project p WHERE p.isActive = true")
    java.util.Map<String, Long> getProjectStatistics();
    
    /**
     * Find projects with budget greater than specified amount
     * @param minBudget the minimum budget
     * @return List of projects with budget >= minBudget
     */
    @Query("SELECT p FROM Project p WHERE p.budget >= :minBudget AND p.isActive = true")
    List<Project> findProjectsWithBudgetGreaterThan(@Param("minBudget") Double minBudget);
}
