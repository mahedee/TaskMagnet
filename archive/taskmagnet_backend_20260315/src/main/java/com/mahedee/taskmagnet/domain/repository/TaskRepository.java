package com.mahedee.taskmagnet.domain.repository;

import com.mahedee.taskmagnet.domain.enums.Priority;
import com.mahedee.taskmagnet.domain.enums.TaskStatus;
import com.mahedee.taskmagnet.domain.model.Task;
import com.mahedee.taskmagnet.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedToAndIsActiveTrue(User assignedTo);

    List<Task> findByTaskCreatorAndIsActiveTrue(User taskCreator);

    List<Task> findByProjectAndIsActiveTrueOrderByCreatedDateDesc(
            com.mahedee.taskmagnet.domain.model.Project project);

    List<Task> findByStatusAndIsActiveTrue(TaskStatus status);

    List<Task> findByPriorityAndIsActiveTrue(Priority priority);
}
