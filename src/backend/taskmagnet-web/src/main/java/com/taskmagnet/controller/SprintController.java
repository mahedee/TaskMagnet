package com.taskmagnet.controller;

import com.taskmagnet.dto.SprintRequest;
import com.taskmagnet.dto.SprintResponse;
import com.taskmagnet.enums.SprintStatus;
import com.taskmagnet.service.ISprintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

    private final ISprintService sprintService;

    public SprintController(ISprintService sprintService) {
        this.sprintService = sprintService;
    }

    @GetMapping
    public ResponseEntity<List<SprintResponse>> getAll() {
        return ResponseEntity.ok(sprintService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sprintService.getById(id));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<SprintResponse>> getByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(sprintService.getByProjectId(projectId));
    }

    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<List<SprintResponse>> getByProjectIdAndStatus(@PathVariable Long projectId,
                                                                       @PathVariable SprintStatus status) {
        return ResponseEntity.ok(sprintService.getByProjectIdAndStatus(projectId, status));
    }

    @GetMapping("/active")
    public ResponseEntity<List<SprintResponse>> getActiveSprints() {
        return ResponseEntity.ok(sprintService.getActiveSprints());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<SprintResponse>> getOverdueSprints() {
        return ResponseEntity.ok(sprintService.getOverdueSprints());
    }

    @PostMapping
    public ResponseEntity<SprintResponse> create(@Valid @RequestBody SprintRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sprintService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SprintResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody SprintRequest request) {
        return ResponseEntity.ok(sprintService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sprintService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<SprintResponse> startSprint(@PathVariable Long id) {
        return ResponseEntity.ok(sprintService.startSprint(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<SprintResponse> completeSprint(@PathVariable Long id) {
        return ResponseEntity.ok(sprintService.completeSprint(id));
    }

    @PostMapping("/{sprintId}/tasks/{taskId}")
    public ResponseEntity<SprintResponse> addTaskToSprint(@PathVariable Long sprintId,
                                                         @PathVariable Long taskId) {
        return ResponseEntity.ok(sprintService.addTaskToSprint(sprintId, taskId));
    }

    @DeleteMapping("/{sprintId}/tasks/{taskId}")
    public ResponseEntity<SprintResponse> removeTaskFromSprint(@PathVariable Long sprintId,
                                                              @PathVariable Long taskId) {
        return ResponseEntity.ok(sprintService.removeTaskFromSprint(sprintId, taskId));
    }

    @PostMapping("/{sprintId}/members/{userId}")
    public ResponseEntity<SprintResponse> addMemberToSprint(@PathVariable Long sprintId,
                                                           @PathVariable Long userId) {
        return ResponseEntity.ok(sprintService.addMemberToSprint(sprintId, userId));
    }

    @DeleteMapping("/{sprintId}/members/{userId}")
    public ResponseEntity<SprintResponse> removeMemberFromSprint(@PathVariable Long sprintId,
                                                                @PathVariable Long userId) {
        return ResponseEntity.ok(sprintService.removeMemberFromSprint(sprintId, userId));
    }
}