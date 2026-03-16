package com.taskmagnet.enums;

public enum ProjectStatus {
    PLANNING,
    ACTIVE,
    ON_HOLD,
    COMPLETED,
    CANCELLED,
    ARCHIVED;

    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED || this == ARCHIVED;
    }
}
