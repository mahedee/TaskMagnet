package com.mahedee.taskmagnet.api.controller;

import com.mahedee.taskmagnet.application.port.in.TaskStatusUseCase;
import com.mahedee.taskmagnet.domain.model.TaskStatusItem;
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
@RequestMapping("/api/taskstatus")
@Tag(name = "Task Status", description = "Task Status management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskStatusController {

    private final TaskStatusUseCase taskStatusUseCase;

    public TaskStatusController(TaskStatusUseCase taskStatusUseCase) {
        this.taskStatusUseCase = taskStatusUseCase;
    }

    @GetMapping
    @Operation(summary = "Get all task statuses")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<TaskStatusItem>> getAllTaskStatuses() {
        return ResponseEntity.ok(taskStatusUseCase.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task status by ID")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TaskStatusItem> getTaskStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(taskStatusUseCase.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a task status")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TaskStatusItem> createTaskStatus(@RequestBody TaskStatusItem taskStatusItem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskStatusUseCase.create(taskStatusItem));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task status")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TaskStatusItem> updateTaskStatus(@PathVariable Long id,
                                                           @RequestBody TaskStatusItem taskStatusItem) {
        return ResponseEntity.ok(taskStatusUseCase.update(id, taskStatusItem));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable Long id) {
        taskStatusUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
