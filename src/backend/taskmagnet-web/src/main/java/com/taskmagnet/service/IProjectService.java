package com.taskmagnet.service;

import com.taskmagnet.dto.ProjectRequest;
import com.taskmagnet.dto.ProjectResponse;

import java.util.List;

public interface IProjectService {

    List<ProjectResponse> getAll();

    ProjectResponse getById(Long id);

    ProjectResponse create(ProjectRequest request);

    ProjectResponse update(Long id, ProjectRequest request);

    void delete(Long id);
}
