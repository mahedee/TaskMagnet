package com.taskmagnet.service;

import com.taskmagnet.dto.ProjectRequest;
import com.taskmagnet.dto.ProjectResponse;
import com.taskmagnet.entity.Category;
import com.taskmagnet.entity.Project;
import com.taskmagnet.entity.User;
import com.taskmagnet.enums.ProjectStatus;
import com.taskmagnet.exception.ResourceNotFoundException;
import com.taskmagnet.repository.CategoryRepository;
import com.taskmagnet.repository.ProjectRepository;
import com.taskmagnet.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements IProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                               UserRepository userRepository,
                               CategoryRepository categoryRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAll() {
        return projectRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return toResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        if (projectRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Project code already in use: " + request.getCode());
        }

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getOwnerId()));

        Project project = new Project();
        project.setName(request.getName());
        project.setCode(request.getCode());
        project.setDescription(request.getDescription());
        project.setStatus(request.getStatus() != null ? request.getStatus() : ProjectStatus.PLANNING);
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setTargetCompletionDate(request.getTargetCompletionDate());
        project.setBudget(request.getBudget());
        project.setColorCode(request.getColorCode());
        project.setOwner(owner);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            project.setCategory(category);
        }

        return toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        if (!project.getCode().equals(request.getCode()) &&
                projectRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Project code already in use: " + request.getCode());
        }

        if (!project.getOwner().getId().equals(request.getOwnerId())) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getOwnerId()));
            project.setOwner(owner);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            project.setCategory(category);
        } else {
            project.setCategory(null);
        }

        project.setName(request.getName());
        project.setCode(request.getCode());
        project.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setTargetCompletionDate(request.getTargetCompletionDate());
        project.setBudget(request.getBudget());
        project.setColorCode(request.getColorCode());

        return toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        project.setDeleted(true);
        projectRepository.save(project);
    }

    private ProjectResponse toResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setCode(project.getCode());
        response.setDescription(project.getDescription());
        response.setStatus(project.getStatus());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setTargetCompletionDate(project.getTargetCompletionDate());
        response.setActualCompletionDate(project.getActualCompletionDate());
        response.setBudget(project.getBudget());
        response.setColorCode(project.getColorCode());
        response.setProgressPercentage(project.getProgressPercentage());
        response.setIsActive(project.getIsActive());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());

        if (project.getOwner() != null) {
            response.setOwnerId(project.getOwner().getId());
            response.setOwnerUsername(project.getOwner().getUsername());
        }
        if (project.getCategory() != null) {
            response.setCategoryId(project.getCategory().getId());
            response.setCategoryName(project.getCategory().getName());
        }

        return response;
    }
}
