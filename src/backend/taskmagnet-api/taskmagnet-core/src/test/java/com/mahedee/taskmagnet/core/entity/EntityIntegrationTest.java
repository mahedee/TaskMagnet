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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for JPA entities to verify the database schema implementation.
 * Tests entity creation, relationships, and audit trail functionality.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
@DataJpaTest
@ActiveProfiles("test")
public class EntityIntegrationTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    private User testUser;
    private Category testCategory;
    private Project testProject;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User("testuser", "test@example.com", "password123", 
                           "John", "Doe", "test-admin");
        testUser.setDepartment("IT");
        testUser.setJobTitle("Software Engineer");
        entityManager.persistAndFlush(testUser);
        
        // Create test category
        testCategory = new Category("Development", "Software development tasks", "test-admin");
        testCategory.setColorCode("#3B82F6");
        testCategory.setIcon("code");
        entityManager.persistAndFlush(testCategory);
        
        // Create test project
        testProject = new Project("Test Project", "TEST-001", 
                                 "A test project for validation", testUser, "test-admin");
        testProject.setCategory(testCategory);
        testProject.setStartDate(LocalDate.now());
        testProject.setTargetCompletionDate(LocalDate.now().plusDays(30));
        entityManager.persistAndFlush(testProject);
    }
    
    @Test
    void testUserEntityCreationAndAuditTrail() {
        // Verify user creation
        assertThat(testUser.getId()).isNotNull();
        assertThat(testUser.getUsername()).isEqualTo("testuser");
        assertThat(testUser.getEmail()).isEqualTo("test@example.com");
        assertThat(testUser.getFullName()).isEqualTo("John Doe");
        
        // Verify audit trail
        assertThat(testUser.getCreatedDate()).isNotNull();
        assertThat(testUser.getModifiedDate()).isNotNull();
        assertThat(testUser.getCreatedBy()).isEqualTo("test-admin");
        assertThat(testUser.getModifiedBy()).isEqualTo("test-admin");
        assertThat(testUser.getIsActive()).isTrue();
        assertThat(testUser.getVersion()).isEqualTo(0L);
    }
    
    @Test
    void testCategoryEntityHierarchy() {
        // Create parent category
        Category parentCategory = new Category("Technology", "Technology related categories", "test-admin");
        entityManager.persistAndFlush(parentCategory);
        
        // Create child category
        Category childCategory = new Category("Frontend", "Frontend development", "test-admin");
        childCategory.setParent(parentCategory);
        entityManager.persistAndFlush(childCategory);
        
        // Verify hierarchy
        assertThat(childCategory.getParent()).isEqualTo(parentCategory);
        assertThat(childCategory.isRootCategory()).isFalse();
        assertThat(childCategory.getLevel()).isEqualTo(1);
        assertThat(childCategory.getFullPath()).isEqualTo("Technology > Frontend");
        
        assertThat(parentCategory.isRootCategory()).isTrue();
        assertThat(parentCategory.getLevel()).isEqualTo(0);
        assertThat(parentCategory.getFullPath()).isEqualTo("Technology");
    }
    
    @Test
    void testProjectEntityAndRelationships() {
        // Verify project creation
        assertThat(testProject.getId()).isNotNull();
        assertThat(testProject.getName()).isEqualTo("Test Project");
        assertThat(testProject.getCode()).isEqualTo("TEST-001");
        assertThat(testProject.getOwner()).isEqualTo(testUser);
        assertThat(testProject.getCategory()).isEqualTo(testCategory);
        assertThat(testProject.getStatus()).isEqualTo(ProjectStatus.PLANNING);
        assertThat(testProject.getProgressPercentage()).isEqualTo(0);
        
        // Test adding members
        User memberUser = new User("member", "member@example.com", "password123", 
                                  "Jane", "Smith", "test-admin");
        entityManager.persistAndFlush(memberUser);
        
        testProject.addMember(memberUser);
        entityManager.persistAndFlush(testProject);
        
        assertThat(testProject.getMembers()).contains(memberUser);
        assertThat(testProject.isMember(memberUser)).isTrue();
        assertThat(testProject.isMember(testUser)).isTrue(); // Owner is also considered a member
    }
    
    @Test
    void testTaskEntityAndRelationships() {
        // Create task
        Task task = new Task("Test Task", "A test task for validation", 
                           testUser, "test-admin");
        task.setPriority(Priority.HIGH);
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setProject(testProject);
        task.setCategory(testCategory);
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setEstimatedHours(8.0);
        
        entityManager.persistAndFlush(task);
        
        // Verify task creation
        assertThat(task.getId()).isNotNull();
        assertThat(task.getTitle()).isEqualTo("Test Task");
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.NOT_STARTED);
        assertThat(task.getCreatedBy()).isEqualTo(testUser);
        assertThat(task.getProject()).isEqualTo(testProject);
        assertThat(task.getCategory()).isEqualTo(testCategory);
        
        // Test task status operations
        assertThat(task.isCompleted()).isFalse();
        assertThat(task.isOverdue()).isFalse();
        assertThat(task.isHighPriority()).isTrue();
        
        // Test task assignment
        task.assignTo(testUser);
        assertThat(task.getAssignedTo()).isEqualTo(testUser);
        assertThat(task.isAssigned()).isTrue();
        
        // Test task completion
        task.markAsCompleted();
        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(task.getCompletionDate()).isNotNull();
        assertThat(task.getProgressPercentage()).isEqualTo(100);
        assertThat(task.isCompleted()).isTrue();
    }
    
    @Test
    void testTaskHierarchy() {
        // Create parent task
        Task parentTask = new Task("Parent Task", "Main task", testUser, "test-admin");
        parentTask.setProject(testProject);
        entityManager.persistAndFlush(parentTask);
        
        // Create subtask
        Task subTask = new Task("Sub Task", "Subtask of main task", testUser, "test-admin");
        subTask.setParentTask(parentTask);
        subTask.setProject(testProject);
        entityManager.persistAndFlush(subTask);
        
        // Verify hierarchy
        assertThat(subTask.getParentTask()).isEqualTo(parentTask);
        assertThat(parentTask.getParentTask()).isNull();
    }
    
    @Test
    void testEnumValues() {
        // Test Priority enum
        assertThat(Priority.HIGH.getLevel()).isEqualTo(3);
        assertThat(Priority.HIGH.getDisplayName()).isEqualTo("High");
        assertThat(Priority.fromLevel(3)).isEqualTo(Priority.HIGH);
        assertThat(Priority.fromDisplayName("High")).isEqualTo(Priority.HIGH);
        
        // Test TaskStatus enum
        assertThat(TaskStatus.COMPLETED.isFinal()).isTrue();
        assertThat(TaskStatus.IN_PROGRESS.isFinal()).isFalse();
        assertThat(TaskStatus.NOT_STARTED.canTransitionTo(TaskStatus.IN_PROGRESS)).isTrue();
        assertThat(TaskStatus.COMPLETED.canTransitionTo(TaskStatus.IN_PROGRESS)).isFalse();
        
        // Test ProjectStatus enum
        assertThat(ProjectStatus.COMPLETED.isFinal()).isTrue();
        assertThat(ProjectStatus.ACTIVE.isFinal()).isFalse();
        assertThat(ProjectStatus.ACTIVE.isActive()).isTrue();
    }
    
    @Test
    void testAuditEntityLifecycle() {
        // Create a new user to test lifecycle methods
        User newUser = new User("lifecycle", "lifecycle@example.com", "password123", 
                               "Test", "User", "test-admin");
        
        // Test @PrePersist
        assertThat(newUser.isNew()).isTrue();
        entityManager.persistAndFlush(newUser);
        assertThat(newUser.isNew()).isFalse();
        assertThat(newUser.getCreatedDate()).isNotNull();
        assertThat(newUser.getModifiedDate()).isNotNull();
        
        // Test @PreUpdate
        LocalDateTime originalModifiedDate = newUser.getModifiedDate();
        newUser.setFirstName("Updated");
        entityManager.persistAndFlush(newUser);
        assertThat(newUser.getModifiedDate()).isAfter(originalModifiedDate);
        
        // Test soft delete
        newUser.softDelete("test-admin");
        assertThat(newUser.getIsActive()).isFalse();
        assertThat(newUser.getModifiedBy()).isEqualTo("test-admin");
        
        // Test activate
        newUser.activate("test-admin");
        assertThat(newUser.getIsActive()).isTrue();
    }
    
    @Test
    void testProjectProgressCalculation() {
        // Create tasks for the project
        Task task1 = new Task("Task 1", "First task", testUser, "test-admin");
        task1.setProject(testProject);
        task1.setStatus(TaskStatus.COMPLETED);
        entityManager.persistAndFlush(task1);
        
        Task task2 = new Task("Task 2", "Second task", testUser, "test-admin");
        task2.setProject(testProject);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        entityManager.persistAndFlush(task2);
        
        Task task3 = new Task("Task 3", "Third task", testUser, "test-admin");
        task3.setProject(testProject);
        task3.setStatus(TaskStatus.COMPLETED);
        entityManager.persistAndFlush(task3);
        
        // Refresh project to get updated tasks
        entityManager.refresh(testProject);
        
        // Verify task counts
        assertThat(testProject.getTaskCount()).isEqualTo(3);
        assertThat(testProject.getCompletedTaskCount()).isEqualTo(2);
        
        // Test progress calculation
        testProject.updateProgress();
        assertThat(testProject.getProgressPercentage()).isEqualTo(66); // 2 out of 3 tasks completed
    }
}
