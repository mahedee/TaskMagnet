package com.mahedee.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahedee.backend.models.User;

// @Repository is a Spring annotation that indicates this interface is a repository and it will be managed by Spring.
//JpaRepository is a Spring Data JPA interface that provides basic CRUD (Create, Read, Update, Delete) operations for entities of type User

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // This method returns an Optional<User> containing the User entity that has the specified username. If no such entity exists, it returns an empty Optional.
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
