package com.mahedee.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "taskCategory")
public class TaskCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 250)
    private String name;
    
    public TaskCategory() {
    }
    public TaskCategory(String name) {
        this.name = name;
    }
    public TaskCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public TaskCategory(TaskCategory taskCategory) {
        this.id = taskCategory.getId();
        this.name = taskCategory.getName();
    }
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name= name;
    }
    // public Object getCategoryName() {
    //     return null;
    // }
    // public void setCategoryName(Object categoryName) {
    // }
}
