package com.mahedee.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "taskStatus")
public class TaskStatus extends BaseEntity<Long> {
    
    @NotBlank
    @Size(max = 250)
    private String name;

    // Getters and setters

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
