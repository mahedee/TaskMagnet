package com.mahedee.taskmagnet.domain.enums;

/**
 * Task priority levels ordered from lowest to highest urgency.
 */
public enum Priority {
    LOW(1, "Low", "Low priority task"),
    MEDIUM(2, "Medium", "Medium priority task"),
    HIGH(3, "High", "High priority task"),
    URGENT(4, "Urgent", "Urgent task requiring prompt attention"),
    CRITICAL(5, "Critical", "Critical task requiring immediate attention");

    private final int level;
    private final String displayName;
    private final String description;

    Priority(int level, String displayName, String description) {
        this.level = level;
        this.displayName = displayName;
        this.description = description;
    }

    public int getLevel() { return level; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
