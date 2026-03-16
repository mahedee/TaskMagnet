package com.mahedee.taskmagnet.domain.repository;

import com.mahedee.taskmagnet.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndIsActiveTrue(String name);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = true ORDER BY c.sortOrder ASC, c.name ASC")
    List<Category> findRootCategories();

    @Query("SELECT c FROM Category c WHERE c.parent = :parent AND c.isActive = true ORDER BY c.sortOrder ASC, c.name ASC")
    List<Category> findByParentOrderBySortOrderAscNameAsc(@Param("parent") Category parent);
}
