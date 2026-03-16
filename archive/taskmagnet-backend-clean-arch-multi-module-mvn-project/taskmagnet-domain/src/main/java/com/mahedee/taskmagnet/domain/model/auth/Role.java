package com.mahedee.taskmagnet.domain.model.auth;

import com.mahedee.taskmagnet.domain.model.common.BaseEntity;
import jakarta.persistence.*;

/**
 * Role entity representing a user role in the system.
 */
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false, unique = true)
    private ERole name;

    public Role() {}

    public Role(ERole name) {
        this.name = name;
    }

    public ERole getName() { return name; }
    public void setName(ERole name) { this.name = name; }
}
