package com.taskmagnet.service;

import com.taskmagnet.dto.TaskRequest;
import com.taskmagnet.dto.TaskResponse;
import com.taskmagnet.entity.Category;
import com.taskmagnet.entity.Project;
import com.taskmagnet.entity.Sprint;
import com.taskmagnet.entity.Task;
import com.taskmagnet.entity.User;
import com.taskmagnet.enums.Priority;
import com.taskmagnet.enums.TaskStatus;
import com.taskmagnet.exception.ResourceNotFoundException;
import com.taskmagnet.repository.CategoryRepository;
import com.taskmagnet.repository.ProjectRepository;
import com.taskmagnet.repository.SprintRepository;
import com.taskmagnet.repository.TaskRepository;
import com.taskmagnet.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements ITaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final SprintRepository sprintRepository;

    public TaskServiceImpl(TaskRepository taskRepository,
                            UserRepository userRepository,
                            ProjectRepository projectRepository,
                            CategoryRepository categoryRepository,
                            SprintRepository sprintRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.sprintRepository = sprintRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAll() {
        return taskRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse create(TaskRequest request) {
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getCreatedById()));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.NOT_STARTED);
        task.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        task.setDueDate(request.getDueDate());
        task.setStartDate(request.getStartDate());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setNotes(request.getNotes());
        task.setTags(request.getTags());
        task.setIsBillable(request.getIsBillable() != null ? request.getIsBillable() : false);
        task.setBillableHours(request.getBillableHours());
        task.setBillableRate(request.getBillableRate());
        task.setCreatedByUser(createdBy);
        task.setCreatedByUsername(createdBy.getUsername());

        resolveRelationships(task, request);

        return toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        task.setDueDate(request.getDueDate());
        task.setStartDate(request.getStartDate());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setNotes(request.getNotes());
        task.setTags(request.getTags());
        if (request.getIsBillable() != null) {
            task.setIsBillable(request.getIsBillable());
        }
        task.setBillableHours(request.getBillableHours());
        task.setBillableRate(request.getBillableRate());

        resolveRelationships(task, request);

        return toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        task.setDeleted(true);
        taskRepository.save(task);
    }

    private void resolveRelationships(Task task, TaskRequest request) {
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssignedToId()));
            task.setAssignedTo(assignedTo);
        } else {
            task.setAssignedTo(null);
        }

        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
            task.setProject(project);
        } else {
            task.setProject(null);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        if (request.getParentTaskId() != null) {
            Task parentTask = taskRepository.findById(request.getParentTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent task not found with id: " + request.getParentTaskId()));
            task.setParentTask(parentTask);
        } else {
            task.setParentTask(null);
        }

        if (request.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(request.getSprintId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + request.getSprintId()));
            task.setSprint(sprint);
        } else {
            task.setSprint(null);
        }
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setStartDate(task.getStartDate());
        response.setCompletionDate(task.getCompletionDate());
        response.setEstimatedHours(task.getEstimatedHours());
        response.setActualHours(task.getActualHours());
        response.setProgressPercentage(task.getProgressPercentage());
        response.setNotes(task.getNotes());
        response.setTags(task.getTags());
        response.setIsBillable(task.getIsBillable());
        response.setBillableHours(task.getBillableHours());
        response.setBillableRate(task.getBillableRate());
        response.setIsActive(task.getIsActive());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        if (task.getAssignedTo() != null) {
            response.setAssignedToId(task.getAssignedTo().getId());
            response.setAssignedToUsername(task.getAssignedTo().getUsername());
        }
        if (task.getCreatedByUser() != null) {
            response.setCreatedById(task.getCreatedByUser().getId());
            response.setCreatedByUsername(task.getCreatedByUser().getUsername());
        }
        if (task.getProject() != null) {
            response.setProjectId(task.getProject().getId());
            response.setProjectName(task.getProject().getName());
        }
        if (task.getCategory() != null) {
            response.setCategoryId(task.getCategory().getId());
            response.setCategoryName(task.getCategory().getName());
        }
        if (task.getParentTask() != null) {
            response.setParentTaskId(task.getParentTask().getId());
            response.setParentTaskTitle(task.getParentTask().getTitle());
        }
        if (task.getSprint() != null) {
            response.setSprintId(task.getSprint().getId());
            response.setSprintName(task.getSprint().getName());
        }

        return response;
    }
}
