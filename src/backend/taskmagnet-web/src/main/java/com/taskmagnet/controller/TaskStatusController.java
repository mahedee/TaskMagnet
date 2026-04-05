package com.taskmagnet.controller;

import com.taskmagnet.dto.TaskStatusRequest;
import com.taskmagnet.dto.TaskStatusResponse;
import com.taskmagnet.service.ITaskStatusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task-statuses")
public class TaskStatusController {

    private final ITaskStatusService taskStatusService;

    public TaskStatusController(ITaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @GetMapping
    public ResponseEntity<List<TaskStatusResponse>> getAll() {
        return ResponseEntity.ok(taskStatusService.getAll());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskStatusResponse>> getByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskStatusService.getByProjectId(projectId));
    }

    @GetMapping("/project/{projectId}/closed/{isClosedStatus}")
    public ResponseEntity<List<TaskStatusResponse>> getByProjectIdAndClosedStatus(
            @PathVariable Long projectId,
            @PathVariable Boolean isClosedStatus) {
        return ResponseEntity.ok(taskStatusService.getByProjectIdAndClosedStatus(projectId, isClosedStatus));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskStatusService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TaskStatusResponse> create(@Valid @RequestBody TaskStatusRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskStatusService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody TaskStatusRequest request) {
        return ResponseEntity.ok(taskStatusService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/project/{projectId}/reorder")
    public ResponseEntity<Void> reorderStatuses(@PathVariable Long projectId,
                                                @RequestBody Map<String, List<Long>> request) {
        List<Long> statusIds = request.get("statusIds");
        if (statusIds == null || statusIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        taskStatusService.reorderStatuses(projectId, statusIds);
        return ResponseEntity.ok().build();
    }
}