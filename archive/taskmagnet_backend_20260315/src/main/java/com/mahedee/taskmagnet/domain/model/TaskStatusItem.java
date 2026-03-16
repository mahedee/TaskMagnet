package com.mahedee.taskmagnet.domain.model;

import com.mahedee.taskmagnet.domain.model.common.BaseEntity;
import jakarta.persistence.*;

/**
 * TaskStatusItem entity — configurable workflow status labels stored in the database.
 * Named TaskStatusItem to avoid collision with the TaskStatus enum in the enums package.
 */
@Entity
@Table(name = "taskStatus")
public class TaskStatusItem extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    public TaskStatusItem() {}

    public TaskStatusItem(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
