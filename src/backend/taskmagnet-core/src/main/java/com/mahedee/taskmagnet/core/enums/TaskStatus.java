package com.mahedee.taskmagnet.core.enums;

/**
 * Enumeration for task status in the TaskMagnet system.
 * Represents the current state of a task in its lifecycle.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
public enum TaskStatus {
    
    NOT_STARTED("Not Started", "Task has been created but not started", false),
    IN_PROGRESS("In Progress", "Task is currently being worked on", false),
    ON_HOLD("On Hold", "Task is temporarily suspended", false),
    COMPLETED("Completed", "Task has been completed successfully", true),
    CANCELLED("Cancelled", "Task has been cancelled and will not be completed", true),
    BLOCKED("Blocked", "Task is blocked by dependencies or issues", false),
    REVIEW("Review", "Task is under review or testing", false),
    APPROVED("Approved", "Task has been reviewed and approved", true);
    
    private final String displayName;
    private final String description;
    private final boolean isFinal;
    
    TaskStatus(String displayName, String description, boolean isFinal) {
        this.displayName = displayName;
        this.description = description;
        this.isFinal = isFinal;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
    
    public boolean isActive() {
        return !isFinal;
    }
    
    /**
     * Get status by display name (case-insensitive)
     * @param displayName the display name
     * @return the corresponding TaskStatus enum
     * @throws IllegalArgumentException if display name is not valid
     */
    public static TaskStatus fromDisplayName(String displayName) {
        for (TaskStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid task status display name: " + displayName);
    }
    
    /**
     * Check if transition from current status to target status is valid
     * @param targetStatus the target status to transition to
     * @return true if transition is valid, false otherwise
     */
    public boolean canTransitionTo(TaskStatus targetStatus) {
        // If current status is final, no transitions allowed except to itself
        if (this.isFinal && this != targetStatus) {
            return false;
        }
        
        // Define valid transitions
        switch (this) {
            case NOT_STARTED:
                return targetStatus == IN_PROGRESS || targetStatus == CANCELLED || targetStatus == ON_HOLD;
            case IN_PROGRESS:
                return targetStatus == COMPLETED || targetStatus == ON_HOLD || targetStatus == BLOCKED || 
                       targetStatus == REVIEW || targetStatus == CANCELLED;
            case ON_HOLD:
                return targetStatus == IN_PROGRESS || targetStatus == CANCELLED || targetStatus == NOT_STARTED;
            case BLOCKED:
                return targetStatus == IN_PROGRESS || targetStatus == CANCELLED || targetStatus == ON_HOLD;
            case REVIEW:
                return targetStatus == APPROVED || targetStatus == IN_PROGRESS || targetStatus == CANCELLED;
            case COMPLETED:
            case CANCELLED:
            case APPROVED:
                return targetStatus == this; // Final states can only stay the same
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
