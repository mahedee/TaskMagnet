package com.mahedee.taskmagnet.domain.repository;

import com.mahedee.taskmagnet.domain.model.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Long> {
}
