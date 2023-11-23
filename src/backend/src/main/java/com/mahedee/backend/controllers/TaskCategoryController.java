package com.mahedee.backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahedee.backend.models.TaskCategory;
import com.mahedee.backend.repository.TaskCategoryRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// * is used as a wildcard, which means that any origin is allowed
// A value of 3600 means that the CORS configuration can be cached by the browser for up to 1 hour.
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/taskcategory")
public class TaskCategoryController {

    @Autowired
    private TaskCategoryRepository taskCategoryRepository;

    @GetMapping("/all")
    public List<TaskCategory> getAllTaskCategory() {
        return taskCategoryRepository.findAll();
    }

    @PostMapping("/create")
    public TaskCategory createTaskCategory(@Valid @RequestBody TaskCategory taskCategory) {
        return taskCategoryRepository.save(taskCategory);
    }

    // @GetMapping("/category/{categoryName}")
    // public List<TaskCategory> getTaskCategoryByCategoryName(@PathVariable("categoryName") String categoryName) {
    //     return taskCategoryRepository.findByCategoryName(categoryName);
    // }

    @GetMapping("/{taskCategoryId}")
    public Optional<TaskCategory> getTaskCategoryById(@PathVariable("taskCategoryId") Long taskCategoryId) {
        return taskCategoryRepository.findById(taskCategoryId);
    }

    @PutMapping("/update/{taskCategoryId}")
    public TaskCategory updateTaskCategory(@PathVariable("taskCategoryId") Long taskCategoryId,
                                           @Valid @RequestBody TaskCategory updatedTaskCategory) {
        return taskCategoryRepository.findById(taskCategoryId)
                .map(taskCategory -> {
                    taskCategory.setName(updatedTaskCategory.getName());
                    return taskCategoryRepository.save(taskCategory);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskCategory not found with id " + taskCategoryId));
    }


    @DeleteMapping("/delete/{taskCategoryId}")
    public ResponseEntity<?> deleteTaskCategory(@PathVariable("taskCategoryId") Long taskCategoryId) {
        return taskCategoryRepository.findById(taskCategoryId)
                .map(taskCategory -> {
                    taskCategoryRepository.delete(taskCategory);
                    return ResponseEntity.ok().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskCategory not found with id " + taskCategoryId));
    }
}
