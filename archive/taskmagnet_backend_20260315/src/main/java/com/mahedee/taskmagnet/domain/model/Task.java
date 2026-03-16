package com.mahedee.taskmagnet.domain.model;

import com.mahedee.taskmagnet.domain.enums.Priority;
import com.mahedee.taskmagnet.domain.enums.TaskStatus;
import com.mahedee.taskmagnet.domain.model.audit.BaseAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task extends BaseAuditEntity {

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private TaskStatus status = TaskStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "estimated_hours", precision = 6, scale = 2)
    private BigDecimal estimatedHours;

    @Column(name = "actual_hours", precision = 6, scale = 2)
    private BigDecimal actualHours;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "is_billable")
    private Boolean isBillable = false;

    @Column(name = "billable_hours", precision = 6, scale = 2)
    private BigDecimal billableHours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    /** The User who created this task (renamed to avoid conflict with BaseAuditEntity.createdBy audit field). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User taskCreator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    public Task() {}

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
    public BigDecimal getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(BigDecimal estimatedHours) { this.estimatedHours = estimatedHours; }
    public BigDecimal getActualHours() { return actualHours; }
    public void setActualHours(BigDecimal actualHours) { this.actualHours = actualHours; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Boolean getIsBillable() { return isBillable; }
    public void setIsBillable(Boolean isBillable) { this.isBillable = isBillable; }
    public BigDecimal getBillableHours() { return billableHours; }
    public void setBillableHours(BigDecimal billableHours) { this.billableHours = billableHours; }
    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }
    public User getTaskCreator() { return taskCreator; }
    public void setTaskCreator(User taskCreator) { this.taskCreator = taskCreator; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Task getParentTask() { return parentTask; }
    public void setParentTask(Task parentTask) { this.parentTask = parentTask; }
}
