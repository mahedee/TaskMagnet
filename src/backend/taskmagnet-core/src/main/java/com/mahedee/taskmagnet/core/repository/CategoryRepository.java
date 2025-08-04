package com.mahedee.taskmagnet.core.repository;

import com.mahedee.taskmagnet.core.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity operations.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find category by name
     * @param name the category name
     * @return Optional containing the category if found
     */
    Optional<Category> findByNameAndIsActiveTrue(String name);
    
    /**
     * Find root categories (categories without parent)
     * @return List of root categories ordered by sort order and name
     */
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = true ORDER BY c.sortOrder ASC, c.name ASC")
    List<Category> findRootCategories();
    
    /**
     * Find child categories of a parent category
     * @param parent the parent category
     * @return List of child categories ordered by sort order and name
     */
    @Query("SELECT c FROM Category c WHERE c.parent = :parent AND c.isActive = true ORDER BY c.sortOrder ASC, c.name ASC")
    List<Category> findByParentOrderBySortOrderAscNameAsc(@Param("parent") Category parent);
    
    /**
     * Find all categories ordered by hierarchy
     * @return List of all categories in hierarchical order
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.parent.id ASC NULLS FIRST, c.sortOrder ASC, c.name ASC")
    List<Category> findAllOrderedHierarchically();
    
    /**
     * Find categories by level (depth in hierarchy)
     * @param level the hierarchy level (0 for root)
     * @return List of categories at the specified level
     */
    @Query("SELECT c FROM Category c WHERE " +
           "(:level = 0 AND c.parent IS NULL) OR " +
           "(:level = 1 AND c.parent IS NOT NULL AND c.parent.parent IS NULL) OR " +
           "(:level = 2 AND c.parent IS NOT NULL AND c.parent.parent IS NOT NULL AND c.parent.parent.parent IS NULL) " +
           "AND c.isActive = true ORDER BY c.sortOrder ASC, c.name ASC")
    List<Category> findCategoriesByLevel(@Param("level") Integer level);
    
    /**
     * Find leaf categories (categories without children)
     * @return List of leaf categories
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND NOT EXISTS " +
           "(SELECT 1 FROM Category child WHERE child.parent = c AND child.isActive = true)")
    List<Category> findLeafCategories();
    
    /**
     * Find categories with children
     * @return List of categories that have children
     */
    @Query("SELECT DISTINCT c FROM Category c WHERE c.isActive = true AND EXISTS " +
           "(SELECT 1 FROM Category child WHERE child.parent = c AND child.isActive = true)")
    List<Category> findCategoriesWithChildren();
    
    /**
     * Find categories by name containing text (case-insensitive)
     * @param name the text to search for in category name
     * @return List of matching categories
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isActive = true ORDER BY c.name ASC")
    List<Category> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Check if category name exists under a specific parent
     * @param name the category name
     * @param parent the parent category (can be null for root categories)
     * @return true if name exists under the parent, false otherwise
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND " +
           "(:parent IS NULL AND c.parent IS NULL OR c.parent = :parent) AND c.isActive = true")
    boolean existsByNameAndParent(@Param("name") String name, @Param("parent") Category parent);
    
    /**
     * Find categories with task count
     * @return List of categories with their task counts
     */
    @Query("SELECT c, COUNT(t) as taskCount FROM Category c LEFT JOIN c.tasks t " +
           "WHERE c.isActive = true GROUP BY c ORDER BY taskCount DESC, c.name ASC")
    List<Object[]> findCategoriesWithTaskCount();
    
    /**
     * Find categories with project count
     * @return List of categories with their project counts
     */
    @Query("SELECT c, COUNT(p) as projectCount FROM Category c LEFT JOIN c.projects p " +
           "WHERE c.isActive = true GROUP BY c ORDER BY projectCount DESC, c.name ASC")
    List<Object[]> findCategoriesWithProjectCount();
    
    /**
     * Count categories by parent
     * @param parent the parent category
     * @return number of child categories
     */
    long countByParentAndIsActiveTrue(Category parent);
    
    /**
     * Count root categories
     * @return number of root categories
     */
    @Query("SELECT COUNT(c) FROM Category c WHERE c.parent IS NULL AND c.isActive = true")
    long countRootCategories();
    
    /**
     * Find categories with no tasks or projects (empty categories)
     * @return List of empty categories
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND " +
           "NOT EXISTS (SELECT 1 FROM Task t WHERE t.category = c AND t.isActive = true) AND " +
           "NOT EXISTS (SELECT 1 FROM Project p WHERE p.category = c AND p.isActive = true)")
    List<Category> findEmptyCategories();
    
    /**
     * Find the maximum sort order for categories under a parent
     * @param parent the parent category (can be null for root categories)
     * @return the maximum sort order value
     */
    @Query("SELECT COALESCE(MAX(c.sortOrder), 0) FROM Category c WHERE " +
           "(:parent IS NULL AND c.parent IS NULL OR c.parent = :parent) AND c.isActive = true")
    Integer findMaxSortOrderByParent(@Param("parent") Category parent);
    
    /**
     * Get category hierarchy path for a category
     * @param categoryId the category ID
     * @return the full hierarchy path
     */
    @Query(value = "WITH RECURSIVE category_path AS (" +
                   "SELECT id, name, parent_id, name as path, 0 as level " +
                   "FROM categories WHERE id = :categoryId AND is_active = 1 " +
                   "UNION ALL " +
                   "SELECT c.id, c.name, c.parent_id, CONCAT(c.name, ' > ', cp.path), cp.level + 1 " +
                   "FROM categories c INNER JOIN category_path cp ON c.id = cp.parent_id " +
                   "WHERE c.is_active = 1) " +
                   "SELECT path FROM category_path ORDER BY level DESC LIMIT 1", 
           nativeQuery = true)
    String getCategoryPath(@Param("categoryId") Long categoryId);
}
