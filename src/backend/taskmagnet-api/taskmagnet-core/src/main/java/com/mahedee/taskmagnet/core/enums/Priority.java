package com.mahedee.taskmagnet.core.enums;

/**
 * Enumeration for task priority levels in the TaskMagnet system.
 * Defines the relative importance of tasks for proper organization and workflow management.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
public enum Priority {
    
    LOW("Low", "Low priority task", 1),
    MEDIUM("Medium", "Medium priority task", 2),
    HIGH("High", "High priority task", 3),
    URGENT("Urgent", "Urgent priority task", 4),
    CRITICAL("Critical", "Critical priority task", 5);
    
    private final String displayName;
    private final String description;
    private final int level;
    
    Priority(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getLevel() {
        return level;
    }
    
    /**
     * Get priority by level value
     * @param level the priority level (1-5)
     * @return the corresponding Priority enum
     * @throws IllegalArgumentException if level is not valid
     */
    public static Priority fromLevel(int level) {
        for (Priority priority : values()) {
            if (priority.level == level) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority level: " + level);
    }
    
    /**
     * Get priority by display name (case-insensitive)
     * @param displayName the display name
     * @return the corresponding Priority enum
     * @throws IllegalArgumentException if display name is not valid
     */
    public static Priority fromDisplayName(String displayName) {
        for (Priority priority : values()) {
            if (priority.displayName.equalsIgnoreCase(displayName)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority display name: " + displayName);
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
