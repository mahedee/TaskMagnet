package com.taskmagnet.repository;

import com.taskmagnet.entity.Sprint;
import com.taskmagnet.enums.SprintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    List<Sprint> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<Sprint> findByProjectIdAndStatusOrderByStartDateDesc(Long projectId, SprintStatus status);

    List<Sprint> findByStatusOrderByStartDateDesc(SprintStatus status);

    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId AND s.name = :name")
    Optional<Sprint> findByProjectIdAndName(@Param("projectId") Long projectId, @Param("name") String name);

    @Query("SELECT s FROM Sprint s WHERE s.status = :status AND s.endDate < :currentDate")
    List<Sprint> findOverdueSprints(@Param("status") SprintStatus status, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId AND s.status IN :statuses")
    List<Sprint> findByProjectIdAndStatusIn(@Param("projectId") Long projectId, @Param("statuses") List<SprintStatus> statuses);

    @Query("SELECT s FROM Sprint s WHERE s.startDate <= :date AND s.endDate >= :date AND s.status = 'ACTIVE'")
    List<Sprint> findActiveSprintsOnDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(s) FROM Sprint s WHERE s.project.id = :projectId")
    Long countByProjectId(@Param("projectId") Long projectId);

    boolean existsByProjectIdAndName(Long projectId, String name);
}