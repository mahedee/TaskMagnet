package com.mahedee.taskmagnet.application.port.in;

import com.mahedee.taskmagnet.domain.model.TaskStatusItem;

import java.util.List;

/**
 * Input port defining task workflow status management use cases.
 */
public interface TaskStatusUseCase {
    List<TaskStatusItem> findAll();
    TaskStatusItem findById(Long id);
    TaskStatusItem create(TaskStatusItem taskStatusItem);
    TaskStatusItem update(Long id, TaskStatusItem taskStatusItem);
    void delete(Long id);
}
