package com.taskmagnet.repository;

import com.taskmagnet.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    Optional<TaskStatus> findByNameAndProjectId(String name, Long projectId);

    boolean existsByNameAndProjectId(String name, Long projectId);

    @Query("SELECT t FROM TaskStatus t WHERE t.project.id = :projectId ORDER BY t.orderIndex ASC, t.name ASC")
    List<TaskStatus> findByProjectIdOrderByOrderIndex(@Param("projectId") Long projectId);

    @Query("SELECT t FROM TaskStatus t WHERE t.project.id = :projectId AND t.isClosedStatus = :isClosedStatus ORDER BY t.orderIndex ASC, t.name ASC")
    List<TaskStatus> findByProjectIdAndIsClosedStatus(@Param("projectId") Long projectId, @Param("isClosedStatus") Boolean isClosedStatus);

    @Query("SELECT MAX(t.orderIndex) FROM TaskStatus t WHERE t.project.id = :projectId")
    Optional<Integer> findMaxOrderIndexByProjectId(@Param("projectId") Long projectId);
}