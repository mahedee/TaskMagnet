package com.taskmagnet.service;

import com.taskmagnet.dto.TaskRequest;
import com.taskmagnet.dto.TaskResponse;

import java.util.List;

public interface ITaskService {

    List<TaskResponse> getAll();

    TaskResponse getById(Long id);

    TaskResponse create(TaskRequest request);

    TaskResponse update(Long id, TaskRequest request);

    void delete(Long id);
}
