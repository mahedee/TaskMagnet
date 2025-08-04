package com.mahedee.taskmagnet.core.entity;

import com.mahedee.taskmagnet.core.entity.audit.BaseAuditEntity;
import com.mahedee.taskmagnet.core.enums.Priority;
import com.mahedee.taskmagnet.core.enums.TaskStatus;
import com.mahedee.taskmagnet.core.enums.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple JPA entity test to verify that entities can be persisted and retrieved correctly.
 * This test validates the basic database schema functionality.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
@DataJpaTest
@ContextConfiguration(classes = SimpleEntityTest.TestConfig.class)
@ActiveProfiles("test")
public class SimpleEntityTest {

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackages = "com.mahedee.taskmagnet.core.entity")
    @EnableJpaRepositories(basePackages = "com.mahedee.taskmagnet.core.repository")
    static class TestConfig {
    }
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void testUserEntityPersistence() {
        // Create a simple user
        User user = new User("testuser", "test@example.com", "password", 
                           "John", "Doe", "system");
        
        // Persist and flush
        User savedUser = entityManager.persistAndFlush(user);
        
        // Verify the user was saved with an ID
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getFirstName()).isEqualTo("John");
        assertThat(savedUser.getLastName()).isEqualTo("Doe");
        
        // Verify audit fields are populated
        assertThat(savedUser.getCreatedDate()).isNotNull();
        assertThat(savedUser.getModifiedDate()).isNotNull();
        assertThat(savedUser.getCreatedBy()).isEqualTo("system");
        assertThat(savedUser.getVersion()).isEqualTo(0L);
        assertThat(savedUser.getIsActive()).isTrue();
    }
    
    @Test
    void testCategoryEntityPersistence() {
        // Create a simple category
        Category category = new Category("Development", "Development tasks", "system");
        category.setColorCode("#3B82F6");
        category.setIcon("code");
        
        // Persist and flush
        Category savedCategory = entityManager.persistAndFlush(category);
        
        // Verify the category was saved
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Development");
        assertThat(savedCategory.getDescription()).isEqualTo("Development tasks");
        assertThat(savedCategory.getColorCode()).isEqualTo("#3B82F6");
        assertThat(savedCategory.getIcon()).isEqualTo("code");
        assertThat(savedCategory.getIsActive()).isTrue();
    }
    
    @Test
    void testProjectEntityPersistence() {
        // Create a project
        User owner = new User("owner", "owner@example.com", "password", 
                            "Jane", "Smith", "system");
        entityManager.persistAndFlush(owner);
        
        Project project = new Project("TEST-001", "Test Project", "A test project", owner, "system");
        project.setStatus(ProjectStatus.PLANNING);
        project.setStartDate(LocalDate.now());
        project.setTargetCompletionDate(LocalDate.now().plusDays(30));
        
        // Persist and flush
        Project savedProject = entityManager.persistAndFlush(project);
        
        // Verify the project was saved
        assertThat(savedProject.getId()).isNotNull();
        assertThat(savedProject.getCode()).isEqualTo("TEST-001");
        assertThat(savedProject.getName()).isEqualTo("Test Project");
        assertThat(savedProject.getStatus()).isEqualTo(ProjectStatus.PLANNING);
        assertThat(savedProject.getOwner()).isEqualTo(owner);
        assertThat(savedProject.getIsActive()).isTrue();
    }
    
    @Test
    void testTaskEntityPersistence() {
        // Create prerequisites
        User assignee = new User("assignee", "assignee@example.com", "password", 
                               "Bob", "Johnson", "system");
        entityManager.persistAndFlush(assignee);
        
        User owner = new User("owner", "owner@example.com", "password", 
                            "Jane", "Smith", "system");
        entityManager.persistAndFlush(owner);
        
        Project project = new Project("TEST-001", "Test Project", "A test project", owner, "system");
        entityManager.persistAndFlush(project);
        
        Category category = new Category("Development", "Development tasks", "system");
        entityManager.persistAndFlush(category);
        
        // Create a task
        Task task = new Task("Test Task", "A test task description", assignee, "system");
        task.setProject(project);
        task.setCategory(category);
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setEstimatedHours(8.0);
        
        // Persist and flush
        Task savedTask = entityManager.persistAndFlush(task);
        
        // Verify the task was saved
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Test Task");
        assertThat(savedTask.getDescription()).isEqualTo("A test task description");
        assertThat(savedTask.getAssignedTo()).isEqualTo(assignee);
        assertThat(savedTask.getProject()).isEqualTo(project);
        assertThat(savedTask.getCategory()).isEqualTo(category);
        assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.NOT_STARTED);
        assertThat(savedTask.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(savedTask.getEstimatedHours()).isEqualTo(8.0);
        assertThat(savedTask.getIsActive()).isTrue();
    }
    
    @Test
    void testEnumValues() {
        // Test Priority enum
        assertThat(Priority.LOW.getLevel()).isEqualTo(1);
        assertThat(Priority.MEDIUM.getLevel()).isEqualTo(2);
        assertThat(Priority.HIGH.getLevel()).isEqualTo(3);
        assertThat(Priority.URGENT.getLevel()).isEqualTo(4);
        assertThat(Priority.CRITICAL.getLevel()).isEqualTo(5);
        
        // Test TaskStatus enum
        assertThat(TaskStatus.NOT_STARTED.isFinal()).isFalse();
        assertThat(TaskStatus.IN_PROGRESS.isFinal()).isFalse();
        assertThat(TaskStatus.COMPLETED.isFinal()).isTrue();
        assertThat(TaskStatus.CANCELLED.isFinal()).isTrue();
        
        // Test ProjectStatus enum
        assertThat(ProjectStatus.PLANNING.isFinal()).isFalse();
        assertThat(ProjectStatus.ACTIVE.isFinal()).isFalse();
        assertThat(ProjectStatus.COMPLETED.isFinal()).isTrue();
        assertThat(ProjectStatus.CANCELLED.isFinal()).isTrue();
    }
}
