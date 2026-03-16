package com.mahedee.taskmagnet.application.port.in;

import com.mahedee.taskmagnet.domain.model.TaskCategory;

import java.util.List;

/**
 * Input port defining task category management use cases.
 */
public interface TaskCategoryUseCase {
    List<TaskCategory> findAll();
    TaskCategory findById(Long id);
    TaskCategory create(TaskCategory taskCategory);
    TaskCategory update(Long id, TaskCategory taskCategory);
    void delete(Long id);
}
