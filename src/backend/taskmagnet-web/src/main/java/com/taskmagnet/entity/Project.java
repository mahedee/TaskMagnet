package com.taskmagnet.entity;

import com.taskmagnet.entity.base.BaseAuditEntity;
import com.taskmagnet.enums.ProjectStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects", uniqueConstraints = {
    @UniqueConstraint(columnNames = "code")
})
@SQLRestriction("is_deleted = false")
public class Project extends BaseAuditEntity {

    @NotBlank(message = "Project name is required")
    @Size(min = 2, max = 200, message = "Project name must be between 2 and 200 characters")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @NotBlank(message = "Project code is required")
    @Size(min = 2, max = 20, message = "Project code must be between 2 and 20 characters")
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.PLANNING;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "target_completion_date")
    private LocalDate targetCompletionDate;

    @Column(name = "actual_completion_date")
    private LocalDate actualCompletionDate;

    @Column(name = "budget")
    private Double budget;

    @Size(max = 7, message = "Color code must not exceed 7 characters")
    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 0;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "project_members",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    // Constructors
    public Project() {
        super();
    }

    public Project(String name, String code, String description, User owner, String createdBy) {
        super(createdBy);
        this.name = name;
        this.code = code;
        this.description = description;
        this.owner = owner;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getTargetCompletionDate() { return targetCompletionDate; }
    public void setTargetCompletionDate(LocalDate targetCompletionDate) { this.targetCompletionDate = targetCompletionDate; }

    public LocalDate getActualCompletionDate() { return actualCompletionDate; }
    public void setActualCompletionDate(LocalDate actualCompletionDate) { this.actualCompletionDate = actualCompletionDate; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Set<Task> getTasks() { return tasks; }
    public void setTasks(Set<Task> tasks) { this.tasks = tasks; }

    public Set<User> getMembers() { return members; }
    public void setMembers(Set<User> members) { this.members = members; }

    // Utility methods
    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
    }

    public void addMember(User user) {
        members.add(user);
        user.getMemberProjects().add(this);
    }

    public void removeMember(User user) {
        members.remove(user);
        user.getMemberProjects().remove(this);
    }

    public boolean isMember(User user) {
        return members.contains(user) || owner.equals(user);
    }

    public long getTaskCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public long getCompletedTaskCount() {
        if (tasks == null) return 0;
        return tasks.stream()
                .filter(task -> task.getStatus() != null && task.getStatus().isFinal())
                .count();
    }

    public boolean isOverdue() {
        return targetCompletionDate != null &&
               targetCompletionDate.isBefore(LocalDate.now()) &&
               !status.isFinal();
    }

    public boolean isCompleted() {
        return status == ProjectStatus.COMPLETED;
    }

    public boolean isActive() {
        return status == ProjectStatus.ACTIVE;
    }

    public void markAsCompleted() {
        this.status = ProjectStatus.COMPLETED;
        this.actualCompletionDate = LocalDate.now();
        this.progressPercentage = 100;
    }

    public void updateProgress() {
        if (tasks == null || tasks.isEmpty()) {
            progressPercentage = 0;
            return;
        }

        long totalTasks = tasks.size();
        long completedTasks = getCompletedTaskCount();
        progressPercentage = (int) ((completedTasks * 100) / totalTasks);

        if (progressPercentage == 100 && !isCompleted()) {
            markAsCompleted();
        }
    }

    @Override
    public String toString() {
        return String.format("Project{id=%d, name='%s', code='%s', status=%s, progress=%d%%, isActive=%s}",
                getId(), name, code, status, progressPercentage, getIsActive());
    }
}
