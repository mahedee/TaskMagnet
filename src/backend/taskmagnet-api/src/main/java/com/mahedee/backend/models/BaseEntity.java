package com.mahedee.backend.models;
import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


// @MappedSuperclass annotation is used to indicate that the class is not an entity on its own but rather a superclass of the entities that will inherit from it.
@MappedSuperclass
public class BaseEntity<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected T id;

    // @Temporal(TemporalType.TIMESTAMP) annotation is added to the createdDate and modifiedDate fields, 
    // indicating that these fields should be mapped to database columns of type TIMESTAMP.
    // The @Temporal annotation must be specified for persistent fields or properties of type java.util.Date
    
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    protected Date updatedAt = new Date();

    public T getId() {
        return id;
    }
    public void setId(T id) {
        this.id = id;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
