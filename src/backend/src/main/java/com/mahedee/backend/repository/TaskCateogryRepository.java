package com.mahedee.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahedee.backend.models.TaskCategory;

@Repository

// It allows developers to define a repository interface with basic CRUD operations (Create, Read, Update, Delete)
// based on the domain object (in this case, TaskCategory). Long is primary key type

public interface TaskCateogryRepository extends JpaRepository<TaskCategory, Long> {

    
}
