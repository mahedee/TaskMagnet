package com.mahedee.taskmagnet.domain.repository;

import com.mahedee.taskmagnet.domain.model.TaskStatusItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStatusItemRepository extends JpaRepository<TaskStatusItem, Long> {
}
