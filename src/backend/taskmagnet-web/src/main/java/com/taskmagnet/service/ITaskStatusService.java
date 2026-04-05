package com.taskmagnet.service;

import com.taskmagnet.dto.TaskStatusRequest;
import com.taskmagnet.dto.TaskStatusResponse;

import java.util.List;

public interface ITaskStatusService {

    List<TaskStatusResponse> getAll();

    List<TaskStatusResponse> getByProjectId(Long projectId);

    List<TaskStatusResponse> getByProjectIdAndClosedStatus(Long projectId, Boolean isClosedStatus);

    TaskStatusResponse getById(Long id);

    TaskStatusResponse create(TaskStatusRequest request);

    TaskStatusResponse update(Long id, TaskStatusRequest request);

    void delete(Long id);

    void reorderStatuses(Long projectId, List<Long> statusIds);
}