package com.taskmagnet.entity;

import com.taskmagnet.entity.base.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "task_statuses")
@SQLRestriction("is_deleted = false")
public class TaskStatus extends BaseAuditEntity {

    @NotBlank(message = "Task status name is required")
    @Size(min = 2, max = 50, message = "Task status name must be between 2 and 50 characters")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(name = "description", length = 255)
    private String description;

    @Size(min = 7, max = 7, message = "Color must be a valid hex code (7 characters)")
    @Column(name = "color", length = 7)
    private String color;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "is_closed_status", nullable = false)
    private Boolean isClosedStatus = false;

    @NotNull(message = "Project is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @NotNull(message = "Created by user is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    // Constructors
    public TaskStatus() {
        super();
    }

    public TaskStatus(String name, String description, String color, Integer orderIndex, 
                     Boolean isClosedStatus, Project project, User createdByUser) {
        super(createdByUser.getUsername());
        this.name = name;
        this.description = description;
        this.color = color;
        this.orderIndex = orderIndex;
        this.isClosedStatus = isClosedStatus;
        this.project = project;
        this.createdByUser = createdByUser;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Boolean getIsClosedStatus() { return isClosedStatus; }
    public void setIsClosedStatus(Boolean isClosedStatus) { this.isClosedStatus = isClosedStatus; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }
}