package com.mahedee.taskmagnet.domain.model;

import com.mahedee.taskmagnet.domain.model.common.BaseEntity;
import jakarta.persistence.*;

/**
 * TaskCategory entity — user-defined task categories stored in the database.
 */
@Entity
@Table(name = "taskCategory")
public class TaskCategory extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    public TaskCategory() {}

    public TaskCategory(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
