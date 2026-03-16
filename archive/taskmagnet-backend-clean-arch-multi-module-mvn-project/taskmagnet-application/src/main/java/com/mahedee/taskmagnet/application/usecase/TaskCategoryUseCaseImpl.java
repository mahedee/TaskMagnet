package com.mahedee.taskmagnet.application.usecase;

import com.mahedee.taskmagnet.application.port.in.TaskCategoryUseCase;
import com.mahedee.taskmagnet.domain.exception.ResourceNotFoundException;
import com.mahedee.taskmagnet.domain.model.TaskCategory;
import com.mahedee.taskmagnet.domain.repository.TaskCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskCategoryUseCaseImpl implements TaskCategoryUseCase {

    private final TaskCategoryRepository taskCategoryRepository;

    public TaskCategoryUseCaseImpl(TaskCategoryRepository taskCategoryRepository) {
        this.taskCategoryRepository = taskCategoryRepository;
    }

    @Override
    public List<TaskCategory> findAll() {
        return taskCategoryRepository.findAll();
    }

    @Override
    public TaskCategory findById(Long id) {
        return taskCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskCategory", "id", id));
    }

    @Override
    @Transactional
    public TaskCategory create(TaskCategory taskCategory) {
        return taskCategoryRepository.save(taskCategory);
    }

    @Override
    @Transactional
    public TaskCategory update(Long id, TaskCategory updated) {
        TaskCategory existing = findById(id);
        existing.setName(updated.getName());
        return taskCategoryRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TaskCategory existing = findById(id);
        taskCategoryRepository.delete(existing);
    }
}
