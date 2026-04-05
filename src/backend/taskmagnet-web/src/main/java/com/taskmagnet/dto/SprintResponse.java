package com.taskmagnet.dto;

import com.taskmagnet.enums.SprintStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SprintResponse {

    private Long id;
    private String name;
    private String description;
    private SprintStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String sprintGoal;
    private Integer totalStoryPoints;
    private Integer completedStoryPoints;
    private Double capacityHours;
    private Double burnedHours;
    private String colorCode;
    private Long projectId;
    private String projectName;
    private Integer taskCount;
    private Double progress;
    private Integer daysRemaining;
    private Integer duration;
    private List<TaskResponse> tasks;
    private List<UserResponse> members;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByUsername;
    private String updatedByUsername;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public Integer getTaskCount() { return taskCount; }
    public void setTaskCount(Integer taskCount) { this.taskCount = taskCount; }

    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }

    public Integer getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(Integer daysRemaining) { this.daysRemaining = daysRemaining; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public List<TaskResponse> getTasks() { return tasks; }
    public void setTasks(List<TaskResponse> tasks) { this.tasks = tasks; }

    public List<UserResponse> getMembers() { return members; }
    public void setMembers(List<UserResponse> members) { this.members = members; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public String getUpdatedByUsername() { return updatedByUsername; }
    public void setUpdatedByUsername(String updatedByUsername) { this.updatedByUsername = updatedByUsername; }
}