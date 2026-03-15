package com.mahedee.taskmagnet.core.enums;

/**
 * Enumeration for project status in the TaskMagnet system.
 * Represents the current state of a project in its lifecycle.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
public enum ProjectStatus {
    
    PLANNING("Planning", "Project is in planning phase", false),
    ACTIVE("Active", "Project is active and tasks are being worked on", false),
    ON_HOLD("On Hold", "Project is temporarily suspended", false),
    COMPLETED("Completed", "Project has been completed successfully", true),
    CANCELLED("Cancelled", "Project has been cancelled", true),
    ARCHIVED("Archived", "Project has been archived for reference", true);
    
    private final String displayName;
    private final String description;
    private final boolean isFinal;
    
    ProjectStatus(String displayName, String description, boolean isFinal) {
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
     * @return the corresponding ProjectStatus enum
     * @throws IllegalArgumentException if display name is not valid
     */
    public static ProjectStatus fromDisplayName(String displayName) {
        for (ProjectStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid project status display name: " + displayName);
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
