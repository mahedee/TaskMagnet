package com.mahedee.taskmagnet.domain.enums;

/**
 * Lifecycle status values for a task.
 */
public enum TaskStatus {
    NOT_STARTED(false),
    IN_PROGRESS(false),
    ON_HOLD(false),
    COMPLETED(true),
    CANCELLED(true),
    BLOCKED(false),
    REVIEW(false),
    APPROVED(true);

    private final boolean isFinal;

    TaskStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() { return isFinal; }
}
