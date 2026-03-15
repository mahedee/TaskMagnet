package com.mahedee.taskmagnet.core.entity;

import com.mahedee.taskmagnet.core.entity.audit.BaseAuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Category entity representing task categories in the TaskMagnet application.
 * Supports hierarchical category structure with parent-child relationships.
 * Extends BaseAuditEntity to inherit audit trail functionality.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
@Entity
@Table(name = "categories", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "parent_id"})
})
public class Category extends BaseAuditEntity {
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;
    
    @Size(max = 7, message = "Color code must not exceed 7 characters")
    @Column(name = "color_code", length = 7)
    private String colorCode;
    
    @Size(max = 50, message = "Icon must not exceed 50 characters")
    @Column(name = "icon", length = 50)
    private String icon;
    
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
    
    // Self-referencing relationship for hierarchical categories
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, name ASC")
    private Set<Category> children = new HashSet<>();
    
    // Relationships with other entities
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();
    
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Project> projects = new HashSet<>();
    
    // Constructors
    public Category() {
        super();
    }
    
    public Category(String name, String description, String createdBy) {
        super(createdBy);
        this.name = name;
        this.description = description;
    }
    
    public Category(String name, String description, Category parent, String createdBy) {
        super(createdBy);
        this.name = name;
        this.description = description;
        this.parent = parent;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getColorCode() {
        return colorCode;
    }
    
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Category getParent() {
        return parent;
    }
    
    public void setParent(Category parent) {
        this.parent = parent;
    }
    
    public Set<Category> getChildren() {
        return children;
    }
    
    public void setChildren(Set<Category> children) {
        this.children = children;
    }
    
    public Set<Task> getTasks() {
        return tasks;
    }
    
    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
    
    public Set<Project> getProjects() {
        return projects;
    }
    
    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }
    
    // Utility methods
    public boolean isRootCategory() {
        return parent == null;
    }
    
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
    
    public boolean isLeafCategory() {
        return !hasChildren();
    }
    
    public int getLevel() {
        int level = 0;
        Category current = this.parent;
        while (current != null) {
            level++;
            current = current.getParent();
        }
        return level;
    }
    
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " > " + name;
    }
    
    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }
    
    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }
    
    public long getTaskCount() {
        return tasks != null ? tasks.size() : 0;
    }
    
    public long getProjectCount() {
        return projects != null ? projects.size() : 0;
    }
    
    public long getTotalTaskCount() {
        long count = getTaskCount();
        if (children != null) {
            for (Category child : children) {
                count += child.getTotalTaskCount();
            }
        }
        return count;
    }
    
    public long getTotalProjectCount() {
        long count = getProjectCount();
        if (children != null) {
            for (Category child : children) {
                count += child.getTotalProjectCount();
            }
        }
        return count;
    }
    
    @Override
    public String toString() {
        return String.format("Category{id=%d, name='%s', fullPath='%s', level=%d, isActive=%s}", 
                getId(), name, getFullPath(), getLevel(), getIsActive());
    }
}
