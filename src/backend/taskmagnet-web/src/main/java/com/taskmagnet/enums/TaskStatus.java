package com.taskmagnet.enums;

public enum TaskStatus {
    NOT_STARTED,
    IN_PROGRESS,
    IN_REVIEW,
    ON_HOLD,
    COMPLETED,
    APPROVED,
    REJECTED,
    CANCELLED;

    public boolean isFinal() {
        return this == COMPLETED || this == APPROVED || this == CANCELLED;
    }

    public boolean canTransitionTo(TaskStatus next) {
        return switch (this) {
            case NOT_STARTED -> next == IN_PROGRESS || next == CANCELLED;
            case IN_PROGRESS -> next == IN_REVIEW || next == ON_HOLD || next == CANCELLED;
            case IN_REVIEW    -> next == COMPLETED || next == REJECTED || next == IN_PROGRESS;
            case ON_HOLD      -> next == IN_PROGRESS || next == CANCELLED;
            case COMPLETED    -> next == APPROVED;
            case REJECTED     -> next == IN_PROGRESS;
            default           -> false;
        };
    }
}
