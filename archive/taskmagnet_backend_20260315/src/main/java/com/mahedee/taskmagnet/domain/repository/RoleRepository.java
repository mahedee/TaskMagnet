package com.mahedee.taskmagnet.domain.repository;

import com.mahedee.taskmagnet.domain.model.auth.ERole;
import com.mahedee.taskmagnet.domain.model.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
    boolean existsByName(ERole name);
}
