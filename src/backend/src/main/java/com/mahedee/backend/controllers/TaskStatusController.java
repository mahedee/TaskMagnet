package com.mahedee.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahedee.backend.models.TaskStatus;
import com.mahedee.backend.repository.TaskStatusRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/taskstatus")
public class TaskStatusController {
    
    // @Autowired: This is a Spring Framework annotation that indicates 
    // the taskStatusRepository instance variable should be automatically 
    // injected by the Spring container with an instance of the TaskStatusRepository bean.
    @Autowired     
    private TaskStatusRepository taskStatusRepository;

    // @GetMapping: This is a Spring Framework annotation used for mapping web requests
    // onto methods in request-handling classes with flexible method signatures.
    // @GetMapping("/all"): This means that if we invoke /api/taskstatus/all,
    // we will get the list of all task status.

    @GetMapping("/all")
    public List<TaskStatus> getAllTaskStatus() {
        return taskStatusRepository.findAll();
    }

    @PostMapping("/create")
    public TaskStatus createTaskStatus(TaskStatus taskStatus) {
        return taskStatusRepository.save(taskStatus);
    }

    @GetMapping("/{taskStatusId}")
    public TaskStatus getTaskStatusById(Long taskStatusId) {
        return taskStatusRepository.findById(taskStatusId).orElseThrow(() -> new RuntimeException("Error: Task Status is not found."));
    }

    @PutMapping("/update/{taskStatusId}")
    public TaskStatus updateTaskStatus(Long taskStatusId, TaskStatus updatedTaskStatus) {
        return taskStatusRepository.findById(taskStatusId)
                .map(taskStatus -> {
                    taskStatus.setName(updatedTaskStatus.getName());
                    return taskStatusRepository.save(taskStatus);
                })
                .orElseThrow(() -> new RuntimeException("Error: Task Status is not found."));
    }

    @DeleteMapping("/delete/{taskStatusId}")
    //@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String deleteTaskStatus(Long taskStatusId) {
        return taskStatusRepository.findById(taskStatusId)
                .map(taskStatus -> {
                    taskStatusRepository.delete(taskStatus);
                    return "Delete Successfully!";
                })
                .orElseThrow(() -> new RuntimeException("Error: Task Status is not found."));
    }

    // @GetMapping("/admin")
    // @PreAuthorize("hasRole('ADMIN')")
    // public String adminAccess() {
    //     return "Admin Board.";
    // }
}
