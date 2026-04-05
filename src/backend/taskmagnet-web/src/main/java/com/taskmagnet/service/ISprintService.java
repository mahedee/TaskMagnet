package com.taskmagnet.service;

import com.taskmagnet.dto.SprintRequest;
import com.taskmagnet.dto.SprintResponse;
import com.taskmagnet.enums.SprintStatus;

import java.util.List;

public interface ISprintService {

    List<SprintResponse> getAll();

    List<SprintResponse> getByProjectId(Long projectId);

    List<SprintResponse> getByProjectIdAndStatus(Long projectId, SprintStatus status);

    SprintResponse getById(Long id);

    SprintResponse create(SprintRequest request);

    SprintResponse update(Long id, SprintRequest request);

    void delete(Long id);

    SprintResponse addTaskToSprint(Long sprintId, Long taskId);

    SprintResponse removeTaskFromSprint(Long sprintId, Long taskId);

    SprintResponse addMemberToSprint(Long sprintId, Long userId);

    SprintResponse removeMemberFromSprint(Long sprintId, Long userId);

    SprintResponse startSprint(Long id);

    SprintResponse completeSprint(Long id);

    List<SprintResponse> getActiveSprints();

    List<SprintResponse> getOverdueSprints();
}