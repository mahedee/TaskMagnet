package com.mahedee.taskmagnet.domain.repository;

import com.mahedee.taskmagnet.domain.enums.ProjectStatus;
import com.mahedee.taskmagnet.domain.model.Category;
import com.mahedee.taskmagnet.domain.model.Project;
import com.mahedee.taskmagnet.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByCodeAndIsActiveTrue(String code);
    List<Project> findByOwnerAndIsActiveTrueOrderByCreatedDateDesc(User owner);
    List<Project> findByStatusAndIsActiveTrue(ProjectStatus status);
    List<Project> findByCategoryAndIsActiveTrueOrderByNameAsc(Category category);
}
