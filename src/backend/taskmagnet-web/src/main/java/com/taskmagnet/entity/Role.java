package com.taskmagnet.entity;

import com.taskmagnet.entity.base.BaseEntity;
import com.taskmagnet.enums.RoleName;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@SQLRestriction("is_deleted = false")
public class Role extends BaseEntity<Long> {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 30)
    private RoleName name;

    @Column(name = "description", length = 200)
    private String description;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    public Role() {}

    public Role(RoleName name, String description) {
        this.name = name;
        this.description = description;
    }

    public RoleName getName() { return name; }
    public void setName(RoleName name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }

    @Override
    public String toString() {
        return String.format("Role{id=%d, name=%s}", getId(), name);
    }
}
