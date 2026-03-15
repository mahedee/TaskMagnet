package com.mahedee.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahedee.backend.models.TaskStatus;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long>  {
    
}
