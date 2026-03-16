package com.mahedee.taskmagnet.application.usecase;

import com.mahedee.taskmagnet.application.port.in.TaskStatusUseCase;
import com.mahedee.taskmagnet.domain.exception.ResourceNotFoundException;
import com.mahedee.taskmagnet.domain.model.TaskStatusItem;
import com.mahedee.taskmagnet.domain.repository.TaskStatusItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskStatusUseCaseImpl implements TaskStatusUseCase {

    private final TaskStatusItemRepository taskStatusItemRepository;

    public TaskStatusUseCaseImpl(TaskStatusItemRepository taskStatusItemRepository) {
        this.taskStatusItemRepository = taskStatusItemRepository;
    }

    @Override
    public List<TaskStatusItem> findAll() {
        return taskStatusItemRepository.findAll();
    }

    @Override
    public TaskStatusItem findById(Long id) {
        return taskStatusItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatusItem", "id", id));
    }

    @Override
    @Transactional
    public TaskStatusItem create(TaskStatusItem taskStatusItem) {
        return taskStatusItemRepository.save(taskStatusItem);
    }

    @Override
    @Transactional
    public TaskStatusItem update(Long id, TaskStatusItem updated) {
        TaskStatusItem existing = findById(id);
        existing.setName(updated.getName());
        return taskStatusItemRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TaskStatusItem existing = findById(id);
        taskStatusItemRepository.delete(existing);
    }
}
