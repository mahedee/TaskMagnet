package com.mahedee.taskmagnet.core.repository;

import com.mahedee.taskmagnet.core.entity.Task;
import com.mahedee.taskmagnet.core.entity.User;
import com.mahedee.taskmagnet.core.entity.Project;
import com.mahedee.taskmagnet.core.enums.Priority;
import com.mahedee.taskmagnet.core.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Task entity operations.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Find tasks by assigned user
     * @param user the assigned user
     * @return List of tasks assigned to the user
     */
    List<Task> findByAssignedToAndIsActiveTrue(User user);
    
    /**
     * Find tasks created by user
     * @param user the user who created the tasks
     * @return List of tasks created by the user
     */
    List<Task> findByCreatedByAndIsActiveTrue(User user);
    
    /**
     * Find tasks by project
     * @param project the project
     * @return List of tasks in the project
     */
    List<Task> findByProjectAndIsActiveTrueOrderByCreatedDateDesc(Project project);
    
    /**
     * Find tasks by status
     * @param status the task status
     * @return List of tasks with the specified status
     */
    List<Task> findByStatusAndIsActiveTrue(TaskStatus status);
    
    /**
     * Find tasks by priority
     * @param priority the task priority
     * @return List of tasks with the specified priority
     */
    List<Task> findByPriorityAndIsActiveTrue(Priority priority);
    
    /**
     * Find overdue tasks
     * @param currentDate the current date to compare against
     * @return List of overdue tasks
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate AND t.status NOT IN ('COMPLETED', 'CANCELLED', 'APPROVED') AND t.isActive = true")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find tasks due today
     * @param date the date to check
     * @return List of tasks due on the specified date
     */
    List<Task> findByDueDateAndIsActiveTrue(LocalDate date);
    
    /**
     * Find tasks due within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return List of tasks due within the date range
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate AND t.isActive = true ORDER BY t.dueDate ASC")
    List<Task> findTasksDueBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Find high priority tasks
     * @return List of high priority tasks (HIGH, URGENT, CRITICAL)
     */
    @Query("SELECT t FROM Task t WHERE t.priority IN ('HIGH', 'URGENT', 'CRITICAL') AND t.isActive = true ORDER BY t.priority DESC, t.dueDate ASC")
    List<Task> findHighPriorityTasks();
    
    /**
     * Find unassigned tasks
     * @return List of unassigned tasks
     */
    List<Task> findByAssignedToIsNullAndIsActiveTrue();
    
    /**
     * Find tasks by project and status
     * @param project the project
     * @param status the task status
     * @return List of tasks matching the criteria
     */
    List<Task> findByProjectAndStatusAndIsActiveTrue(Project project, TaskStatus status);
    
    /**
     * Find tasks by assigned user and status
     * @param user the assigned user
     * @param status the task status
     * @return List of tasks matching the criteria
     */
    List<Task> findByAssignedToAndStatusAndIsActiveTrue(User user, TaskStatus status);
    
    /**
     * Find completed tasks by user
     * @param user the user
     * @return List of completed tasks by the user
     */
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND t.status IN ('COMPLETED', 'APPROVED') AND t.isActive = true")
    List<Task> findCompletedTasksByUser(@Param("user") User user);
    
    /**
     * Find in-progress tasks by user
     * @param user the user
     * @return List of in-progress tasks by the user
     */
    List<Task> findByAssignedToAndStatusInProgressAndIsActiveTrue(User user);
    
    /**
     * Find billable tasks
     * @return List of billable tasks
     */
    List<Task> findByIsBillableTrueAndIsActiveTrue();
    
    /**
     * Find tasks with parent task (sub-tasks)
     * @param parentTask the parent task
     * @return List of sub-tasks
     */
    List<Task> findByParentTaskAndIsActiveTrue(Task parentTask);
    
    /**
     * Find root tasks (tasks without parent)
     * @return List of root tasks
     */
    List<Task> findByParentTaskIsNullAndIsActiveTrue();
    
    /**
     * Search tasks by title containing text
     * @param title the text to search for in title
     * @return List of matching tasks
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')) AND t.isActive = true")
    List<Task> findByTitleContainingIgnoreCase(@Param("title") String title);
    
    /**
     * Count tasks by status
     * @param status the task status
     * @return number of tasks with the specified status
     */
    long countByStatusAndIsActiveTrue(TaskStatus status);
    
    /**
     * Count tasks by assigned user
     * @param user the assigned user
     * @return number of tasks assigned to the user
     */
    long countByAssignedToAndIsActiveTrue(User user);
    
    /**
     * Count overdue tasks
     * @param currentDate the current date
     * @return number of overdue tasks
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < :currentDate AND t.status NOT IN ('COMPLETED', 'CANCELLED', 'APPROVED') AND t.isActive = true")
    long countOverdueTasks(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Get task statistics for dashboard
     * @return Map containing task statistics
     */
    @Query("SELECT new map(" +
           "SUM(CASE WHEN t.status = 'NOT_STARTED' THEN 1 ELSE 0 END) as notStarted, " +
           "SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as inProgress, " +
           "SUM(CASE WHEN t.status IN ('COMPLETED', 'APPROVED') THEN 1 ELSE 0 END) as completed, " +
           "SUM(CASE WHEN t.dueDate < CURRENT_DATE AND t.status NOT IN ('COMPLETED', 'CANCELLED', 'APPROVED') THEN 1 ELSE 0 END) as overdue" +
           ") FROM Task t WHERE t.isActive = true")
    java.util.Map<String, Long> getTaskStatistics();
}
