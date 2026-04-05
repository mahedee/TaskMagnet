package com.taskmagnet.entity;

import com.taskmagnet.entity.base.BaseAuditEntity;
import com.taskmagnet.enums.Priority;
import com.taskmagnet.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@SQLRestriction("is_deleted = false")
public class Task extends BaseAuditEntity {

    @NotBlank(message = "Task title is required")
    @Size(min = 2, max = 200, message = "Task title must be between 2 and 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(name = "description", length = 2000)
    private String description;

    @NotNull(message = "Task status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status = TaskStatus.NOT_STARTED;

    @NotNull(message = "Task priority is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "estimated_hours")
    private Double estimatedHours;

    @Column(name = "actual_hours")
    private Double actualHours;

    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 0;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Column(name = "notes", length = 1000)
    private String notes;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "is_billable", nullable = false)
    private Boolean isBillable = false;

    @Column(name = "billable_hours")
    private Double billableHours;

    @Column(name = "billable_rate")
    private Double billableRate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Task parentTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Sprint sprint;

    // Constructors
    public Task() {
        super();
    }

    public Task(String title, String description, User createdByUser, String createdByUsername) {
        super(createdByUsername);
        this.title = title;
        this.description = description;
        this.createdBy = createdByUser;
    }

    public Task(String title, String description, Priority priority, User assignedTo, User createdByUser, String createdByUsername) {
        super(createdByUsername);
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.assignedTo = assignedTo;
        this.createdBy = createdByUser;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }

    public Double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }

    public Double getActualHours() { return actualHours; }
    public void setActualHours(Double actualHours) { this.actualHours = actualHours; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Boolean getIsBillable() { return isBillable; }
    public void setIsBillable(Boolean isBillable) { this.isBillable = isBillable; }

    public Double getBillableHours() { return billableHours; }
    public void setBillableHours(Double billableHours) { this.billableHours = billableHours; }

    public Double getBillableRate() { return billableRate; }
    public void setBillableRate(Double billableRate) { this.billableRate = billableRate; }

    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }

    public User getCreatedByUser() { return createdBy; }
    public void setCreatedByUser(User createdBy) { this.createdBy = createdBy; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Task getParentTask() { return parentTask; }
    public void setParentTask(Task parentTask) { this.parentTask = parentTask; }

    public Sprint getSprint() { return sprint; }
    public void setSprint(Sprint sprint) { this.sprint = sprint; }

    // Utility methods
    public boolean isCompleted() {
        return status != null && status.isFinal() &&
               (status == TaskStatus.COMPLETED || status == TaskStatus.APPROVED);
    }

    public boolean isOverdue() {
        return dueDate != null &&
               dueDate.isBefore(LocalDate.now()) &&
               !isCompleted();
    }

    public boolean isAssigned() {
        return assignedTo != null;
    }

    public boolean isInProgress() {
        return status == TaskStatus.IN_PROGRESS;
    }

    public boolean isHighPriority() {
        return priority == Priority.HIGH || priority == Priority.URGENT || priority == Priority.CRITICAL;
    }

    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
        this.completionDate = LocalDateTime.now();
        this.progressPercentage = 100;
    }

    public void markAsStarted() {
        if (this.status == TaskStatus.NOT_STARTED) {
            this.status = TaskStatus.IN_PROGRESS;
            this.startDate = LocalDate.now();
        }
    }

    public void assignTo(User user) {
        this.assignedTo = user;
        if (user != null && !user.getAssignedTasks().contains(this)) {
            user.getAssignedTasks().add(this);
        }
    }

    public void unassign() {
        if (this.assignedTo != null) {
            this.assignedTo.getAssignedTasks().remove(this);
            this.assignedTo = null;
        }
    }

    public Double calculateTotalBillableAmount() {
        if (!isBillable || billableHours == null || billableRate == null) {
            return 0.0;
        }
        return billableHours * billableRate;
    }

    public long getDaysUntilDue() {
        if (dueDate == null) {
            return Long.MAX_VALUE;
        }
        return LocalDate.now().until(dueDate).getDays();
    }

    public boolean canTransitionTo(TaskStatus newStatus) {
        return this.status != null && this.status.canTransitionTo(newStatus);
    }

    @Override
    public String toString() {
        return String.format("Task{id=%d, title='%s', status=%s, priority=%s, assignedTo=%s, dueDate=%s, isActive=%s}",
                getId(), title, status, priority,
                assignedTo != null ? assignedTo.getUsername() : "Unassigned",
                dueDate, getIsActive());
    }
}
