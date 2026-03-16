package com.mahedee.taskmagnet.api.controller;

import com.mahedee.taskmagnet.application.port.in.TaskCategoryUseCase;
import com.mahedee.taskmagnet.domain.model.TaskCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/taskcategory")
@Tag(name = "Task Category", description = "Task Category management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskCategoryController {

    private final TaskCategoryUseCase taskCategoryUseCase;

    public TaskCategoryController(TaskCategoryUseCase taskCategoryUseCase) {
        this.taskCategoryUseCase = taskCategoryUseCase;
    }

    @GetMapping
    @Operation(summary = "Get all task categories")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<TaskCategory>> getAllTaskCategories() {
        return ResponseEntity.ok(taskCategoryUseCase.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task category by ID")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TaskCategory> getTaskCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(taskCategoryUseCase.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a task category")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TaskCategory> createTaskCategory(@RequestBody TaskCategory taskCategory) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskCategoryUseCase.create(taskCategory));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task category")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TaskCategory> updateTaskCategory(@PathVariable Long id,
                                                           @RequestBody TaskCategory taskCategory) {
        return ResponseEntity.ok(taskCategoryUseCase.update(id, taskCategory));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTaskCategory(@PathVariable Long id) {
        taskCategoryUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
