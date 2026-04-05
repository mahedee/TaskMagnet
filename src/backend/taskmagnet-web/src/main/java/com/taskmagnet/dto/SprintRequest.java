package com.taskmagnet.dto;

import com.taskmagnet.enums.SprintStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public class SprintRequest {

    @NotBlank(message = "Sprint name is required")
    @Size(min = 2, max = 200, message = "Sprint name must be between 2 and 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private SprintStatus status;
    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = 500, message = "Sprint goal must not exceed 500 characters")
    private String sprintGoal;

    private Integer totalStoryPoints;
    private Integer completedStoryPoints;
    private Double capacityHours;
    private Double burnedHours;

    @Size(max = 7, message = "Color code must not exceed 7 characters")
    private String colorCode;

    private Set<Long> memberIds;
    private Set<Long> taskIds;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

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

    public Set<Long> getMemberIds() { return memberIds; }
    public void setMemberIds(Set<Long> memberIds) { this.memberIds = memberIds; }

    public Set<Long> getTaskIds() { return taskIds; }
    public void setTaskIds(Set<Long> taskIds) { this.taskIds = taskIds; }
}