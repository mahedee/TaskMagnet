package com.mahedee.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity<Integer> {

    // The EnumType.STRING argument means that the enum values will be stored in the database as strings instead of integers.
    // The @Column(length = 20) annotation indicates that the name field will be mapped to a database column with a maximum length of 20 characters. 
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    public Role(){

    }
    public Role(ERole name){
        this.name = name;
    }

    // Getters and setters
    public ERole getName() {
        return name;
    }
    public void setName(ERole name) {
        this.name = name;
    }
    
}
