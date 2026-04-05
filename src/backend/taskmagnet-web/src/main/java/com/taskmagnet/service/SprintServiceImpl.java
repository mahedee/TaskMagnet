package com.taskmagnet.service;

import com.taskmagnet.dto.SprintRequest;
import com.taskmagnet.dto.SprintResponse;
import com.taskmagnet.dto.TaskResponse;
import com.taskmagnet.dto.UserResponse;
import com.taskmagnet.entity.Project;
import com.taskmagnet.entity.Sprint;
import com.taskmagnet.entity.Task;
import com.taskmagnet.entity.User;
import com.taskmagnet.enums.SprintStatus;
import com.taskmagnet.exception.ResourceNotFoundException;
import com.taskmagnet.repository.ProjectRepository;
import com.taskmagnet.repository.SprintRepository;
import com.taskmagnet.repository.TaskRepository;
import com.taskmagnet.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprintServiceImpl implements ISprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public SprintServiceImpl(SprintRepository sprintRepository,
                             ProjectRepository projectRepository,
                             TaskRepository taskRepository,
                             UserRepository userRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SprintResponse> getAll() {
        return sprintRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SprintResponse> getByProjectId(Long projectId) {
        return sprintRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SprintResponse> getByProjectIdAndStatus(Long projectId, SprintStatus status) {
        return sprintRepository.findByProjectIdAndStatusOrderByStartDateDesc(projectId, status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SprintResponse getById(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + id));
        return toResponseWithDetails(sprint);
    }

    @Override
    @Transactional
    public SprintResponse create(SprintRequest request) {
        // Validate unique name per project
        if (sprintRepository.existsByProjectIdAndName(request.getProjectId(), request.getName())) {
            throw new IllegalArgumentException("Sprint name already exists in this project");
        }

        Sprint sprint = new Sprint();
        mapRequestToEntity(sprint, request);

        Sprint savedSprint = sprintRepository.save(sprint);
        return toResponse(savedSprint);
    }

    @Override
    @Transactional
    public SprintResponse update(Long id, SprintRequest request) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + id));

        // Check unique name if changed
        if (!sprint.getName().equals(request.getName()) &&
            sprintRepository.existsByProjectIdAndName(request.getProjectId(), request.getName())) {
            throw new IllegalArgumentException("Sprint name already exists in this project");
        }

        mapRequestToEntity(sprint, request);
        Sprint savedSprint = sprintRepository.save(sprint);
        return toResponse(savedSprint);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + id));
        
        // Remove sprint from all tasks
        sprint.getTasks().forEach(task -> task.setSprint(null));
        
        sprint.setDeleted(true);
        sprintRepository.save(sprint);
    }

    @Override
    @Transactional
    public SprintResponse addTaskToSprint(Long sprintId, Long taskId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + sprintId));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        // Validate task belongs to same project as sprint
        if (!task.getProject().getId().equals(sprint.getProject().getId())) {
            throw new IllegalArgumentException("Task must belong to the same project as the sprint");
        }

        sprint.addTask(task);
        sprintRepository.save(sprint);
        return toResponse(sprint);
    }

    @Override
    @Transactional
    public SprintResponse removeTaskFromSprint(Long sprintId, Long taskId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + sprintId));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        sprint.removeTask(task);
        sprintRepository.save(sprint);
        return toResponse(sprint);
    }

    @Override
    @Transactional
    public SprintResponse addMemberToSprint(Long sprintId, Long userId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + sprintId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        sprint.addMember(user);
        sprintRepository.save(sprint);
        return toResponse(sprint);
    }

    @Override
    @Transactional
    public SprintResponse removeMemberFromSprint(Long sprintId, Long userId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + sprintId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        sprint.removeMember(user);
        sprintRepository.save(sprint);
        return toResponse(sprint);
    }

    @Override
    @Transactional
    public SprintResponse startSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + id));

        if (sprint.getStatus() != SprintStatus.PLANNING) {
            throw new IllegalStateException("Only sprints in PLANNING status can be started");
        }

        sprint.setStatus(SprintStatus.ACTIVE);
        if (sprint.getStartDate() == null) {
            sprint.setStartDate(LocalDate.now());
        }

        Sprint savedSprint = sprintRepository.save(sprint);
        return toResponse(savedSprint);
    }

    @Override
    @Transactional
    public SprintResponse completeSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + id));

        if (sprint.getStatus() != SprintStatus.ACTIVE) {
            throw new IllegalStateException("Only active sprints can be completed");
        }

        sprint.setStatus(SprintStatus.COMPLETED);
        Sprint savedSprint = sprintRepository.save(sprint);
        return toResponse(savedSprint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SprintResponse> getActiveSprints() {
        return sprintRepository.findByStatusOrderByStartDateDesc(SprintStatus.ACTIVE).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SprintResponse> getOverdueSprints() {
        return sprintRepository.findOverdueSprints(SprintStatus.ACTIVE, LocalDate.now()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void mapRequestToEntity(Sprint sprint, SprintRequest request) {
        sprint.setName(request.getName());
        sprint.setDescription(request.getDescription());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        sprint.setSprintGoal(request.getSprintGoal());
        sprint.setTotalStoryPoints(request.getTotalStoryPoints() != null ? request.getTotalStoryPoints() : 0);
        sprint.setCompletedStoryPoints(request.getCompletedStoryPoints() != null ? request.getCompletedStoryPoints() : 0);
        sprint.setCapacityHours(request.getCapacityHours());
        sprint.setBurnedHours(request.getBurnedHours() != null ? request.getBurnedHours() : 0.0);
        sprint.setColorCode(request.getColorCode() != null ? request.getColorCode() : "#3498db");

        if (request.getStatus() != null) {
            sprint.setStatus(request.getStatus());
        }

        // Set project relationship
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
        sprint.setProject(project);

        // Handle members if provided
        if (request.getMemberIds() != null) {
            sprint.getMembers().clear();
            request.getMemberIds().forEach(userId -> {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                sprint.addMember(user);
            });
        }
    }

    private SprintResponse toResponse(Sprint sprint) {
        SprintResponse response = new SprintResponse();
        response.setId(sprint.getId());
        response.setName(sprint.getName());
        response.setDescription(sprint.getDescription());
        response.setStatus(sprint.getStatus());
        response.setStartDate(sprint.getStartDate());
        response.setEndDate(sprint.getEndDate());
        response.setSprintGoal(sprint.getSprintGoal());
        response.setTotalStoryPoints(sprint.getTotalStoryPoints());
        response.setCompletedStoryPoints(sprint.getCompletedStoryPoints());
        response.setCapacityHours(sprint.getCapacityHours());
        response.setBurnedHours(sprint.getBurnedHours());
        response.setColorCode(sprint.getColorCode());
        response.setTaskCount(sprint.getTasks().size());
        response.setProgress(sprint.getProgress());
        response.setDaysRemaining(sprint.getDaysRemaining());
        response.setDuration(sprint.getDuration());
        response.setIsActive(sprint.getIsActive());
        response.setCreatedAt(sprint.getCreatedAt());
        response.setUpdatedAt(sprint.getUpdatedAt());
        response.setCreatedByUsername(sprint.getCreatedByUsername());
        response.setUpdatedByUsername(sprint.getUpdatedByUsername());

        if (sprint.getProject() != null) {
            response.setProjectId(sprint.getProject().getId());
            response.setProjectName(sprint.getProject().getName());
        }

        return response;
    }

    private SprintResponse toResponseWithDetails(Sprint sprint) {
        SprintResponse response = toResponse(sprint);

        // Add tasks details
        List<TaskResponse> taskResponses = sprint.getTasks().stream()
                .map(this::taskToResponse)
                .collect(Collectors.toList());
        response.setTasks(taskResponses);

        // Add members details
        List<UserResponse> memberResponses = sprint.getMembers().stream()
                .map(this::userToResponse)
                .collect(Collectors.toList());
        response.setMembers(memberResponses);

        return response;
    }

    private TaskResponse taskToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setEstimatedHours(task.getEstimatedHours());
        response.setActualHours(task.getActualHours());
        response.setProgressPercentage(task.getProgressPercentage());

        if (task.getAssignedTo() != null) {
            response.setAssignedToId(task.getAssignedTo().getId());
            response.setAssignedToUsername(task.getAssignedTo().getUsername());
        }

        return response;
    }

    private UserResponse userToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        return response;
    }
}