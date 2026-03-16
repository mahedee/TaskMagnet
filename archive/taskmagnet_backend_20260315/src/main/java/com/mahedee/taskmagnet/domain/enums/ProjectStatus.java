package com.mahedee.taskmagnet.domain.enums;

/**
 * Lifecycle status values for a project.
 */
public enum ProjectStatus {
    PLANNING(false),
    ACTIVE(false),
    ON_HOLD(false),
    COMPLETED(true),
    CANCELLED(true),
    ARCHIVED(true);

    private final boolean isFinal;

    ProjectStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() { return isFinal; }
}
