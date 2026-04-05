package com.taskmagnet.entity;

import com.taskmagnet.entity.base.BaseAuditEntity;
import com.taskmagnet.enums.SprintStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sprints", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "project_id"})
})
@SQLRestriction("is_deleted = false")
public class Sprint extends BaseAuditEntity {

    @NotBlank(message = "Sprint name is required")
    @Size(min = 2, max = 200, message = "Sprint name must be between 2 and 200 characters")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)  
    private String description;

    @NotNull(message = "Sprint status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SprintStatus status = SprintStatus.PLANNING;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "sprint_goal", length = 500)
    private String sprintGoal;

    @Column(name = "story_points")
    private Integer totalStoryPoints = 0;

    @Column(name = "completed_story_points")
    private Integer completedStoryPoints = 0;

    @Column(name = "capacity_hours")
    private Double capacityHours;

    @Column(name = "burned_hours")
    private Double burnedHours = 0.0;

    @Size(max = 7, message = "Color code must not exceed 7 characters")
    @Column(name = "color_code", length = 7, nullable = false)
    private String colorCode = "#3498db";

    // Relationships
    @NotNull(message = "Project is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "sprint", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sprint_members",
        joinColumns = @JoinColumn(name = "sprint_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    // Constructors
    public Sprint() {
        super();
    }

    public Sprint(String name, String description, Project project, String createdByUsername) {
        super(createdByUsername);
        this.name = name;
        this.description = description;
        this.project = project;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SprintStatus getStatus() { return status; }
    public void setStatus(SprintStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getSprintGoal() { return sprintGoal; }
    public void setSprintGoal(String sprintGoal) { this.sprintGoal = sprintGoal; }

    public Integer getTotalStoryPoints() { return totalStoryPoints; }
    public void setTotalStoryPoints(Integer totalStoryPoints) { this.totalStoryPoints = totalStoryPoints; }

    public Integer getCompletedStoryPoints() { return completedStoryPoints; }
    public void setCompletedStoryPoints(Integer completedStoryPoints) { this.completedStoryPoints = completedStoryPoints; }

    public Double getCapacityHours() { return capacityHours; }
    public void setCapacityHours(Double capacityHours) { this.capacityHours = capacityHours; }

    public Double getBurnedHours() { return burnedHours; }
    public void setBurnedHours(Double burnedHours) { this.burnedHours = burnedHours; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Set<Task> getTasks() { return tasks; }
    public void setTasks(Set<Task> tasks) { this.tasks = tasks; }

    public Set<User> getMembers() { return members; }
    public void setMembers(Set<User> members) { this.members = members; }

    // Helper methods
    public void addTask(Task task) {
        tasks.add(task);
        task.setSprint(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setSprint(null);
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void removeMember(User user) {
        members.remove(user);
    }

    public Double getProgress() {
        if (totalStoryPoints == null || totalStoryPoints == 0) {
            return 0.0;
        }
        return (completedStoryPoints.doubleValue() / totalStoryPoints.doubleValue()) * 100.0;
    }

    public Integer getDaysRemaining() {
        if (endDate == null) {
            return null;
        }
        LocalDate today = LocalDate.now();
        if (today.isAfter(endDate)) {
            return 0;
        }
        return Math.toIntExact(today.until(endDate).getDays());
    }

    public Integer getDuration() {
        if (startDate == null || endDate == null) {
            return null;
        }
        return Math.toIntExact(startDate.until(endDate).getDays()) + 1;
    }
}