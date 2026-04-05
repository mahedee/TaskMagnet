package com.taskmagnet.service;

import com.taskmagnet.dto.TaskStatusRequest;
import com.taskmagnet.dto.TaskStatusResponse;
import com.taskmagnet.entity.Project;
import com.taskmagnet.entity.TaskStatus;
import com.taskmagnet.entity.User;
import com.taskmagnet.exception.ResourceNotFoundException;
import com.taskmagnet.repository.ProjectRepository;
import com.taskmagnet.repository.TaskStatusRepository;
import com.taskmagnet.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskStatusServiceImpl implements ITaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskStatusServiceImpl(TaskStatusRepository taskStatusRepository,
                                 ProjectRepository projectRepository,
                                 UserRepository userRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskStatusResponse> getAll() {
        return taskStatusRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskStatusResponse> getByProjectId(Long projectId) {
        return taskStatusRepository.findByProjectIdOrderByOrderIndex(projectId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskStatusResponse> getByProjectIdAndClosedStatus(Long projectId, Boolean isClosedStatus) {
        return taskStatusRepository.findByProjectIdAndIsClosedStatus(projectId, isClosedStatus).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskStatusResponse getById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Status not found with id: " + id));
        return toResponse(taskStatus);
    }

    @Override
    @Transactional
    public TaskStatusResponse create(TaskStatusRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));

        if (taskStatusRepository.existsByNameAndProjectId(request.getName(), request.getProjectId())) {
            throw new IllegalArgumentException("Task status already exists with name '" 
                + request.getName() + "' in this project");
        }

        User currentUser = getCurrentUser();
        
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(request.getName());
        taskStatus.setDescription(request.getDescription());
        taskStatus.setColor(request.getColor() != null ? request.getColor() : getDefaultColor());
        taskStatus.setIsClosedStatus(request.getIsClosedStatus() != null ? request.getIsClosedStatus() : false);
        taskStatus.setProject(project);
        taskStatus.setCreatedByUser(currentUser);

        // Set order index
        if (request.getOrderIndex() != null) {
            taskStatus.setOrderIndex(request.getOrderIndex());
        } else {
            Integer maxOrder = taskStatusRepository.findMaxOrderIndexByProjectId(request.getProjectId())
                    .orElse(0);
            taskStatus.setOrderIndex(maxOrder + 1);
        }

        return toResponse(taskStatusRepository.save(taskStatus));
    }

    @Override
    @Transactional
    public TaskStatusResponse update(Long id, TaskStatusRequest request) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Status not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!taskStatus.getName().equals(request.getName()) &&
                taskStatusRepository.existsByNameAndProjectId(request.getName(), taskStatus.getProject().getId())) {
            throw new IllegalArgumentException("Task status already exists with name '" 
                + request.getName() + "' in this project");
        }

        taskStatus.setName(request.getName());
        taskStatus.setDescription(request.getDescription());
        taskStatus.setColor(request.getColor() != null ? request.getColor() : taskStatus.getColor());
        taskStatus.setIsClosedStatus(request.getIsClosedStatus() != null ? request.getIsClosedStatus() : taskStatus.getIsClosedStatus());
        
        if (request.getOrderIndex() != null) {
            taskStatus.setOrderIndex(request.getOrderIndex());
        }

        return toResponse(taskStatusRepository.save(taskStatus));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Status not found with id: " + id));
        taskStatus.setDeleted(true);
        taskStatus.setIsActive(false);
        taskStatusRepository.save(taskStatus);
    }

    @Override
    @Transactional
    public void reorderStatuses(Long projectId, List<Long> statusIds) {
        for (int i = 0; i < statusIds.size(); i++) {
            Long statusId = statusIds.get(i);
            TaskStatus taskStatus = taskStatusRepository.findById(statusId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task Status not found with id: " + statusId));
            
            if (!taskStatus.getProject().getId().equals(projectId)) {
                throw new IllegalArgumentException("Task Status with id " + statusId + " does not belong to project " + projectId);
            }
            
            taskStatus.setOrderIndex(i + 1);
            taskStatusRepository.save(taskStatus);
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found: " + username));
    }

    private String getDefaultColor() {
        return "#6c757d"; // Default gray color
    }

    private TaskStatusResponse toResponse(TaskStatus taskStatus) {
        TaskStatusResponse response = new TaskStatusResponse();
        response.setId(taskStatus.getId());
        response.setName(taskStatus.getName());
        response.setDescription(taskStatus.getDescription());
        response.setColor(taskStatus.getColor());
        response.setOrderIndex(taskStatus.getOrderIndex());
        response.setIsClosedStatus(taskStatus.getIsClosedStatus());
        response.setIsActive(taskStatus.getIsActive());
        response.setCreatedByUsername(taskStatus.getCreatedByUsername());
        response.setCreatedAt(taskStatus.getCreatedAt());
        response.setUpdatedAt(taskStatus.getUpdatedAt());

        if (taskStatus.getProject() != null) {
            response.setProjectId(taskStatus.getProject().getId());
            response.setProjectName(taskStatus.getProject().getName());
        }

        return response;
    }
}