package com.mahedee.taskmagnet.core.entity.audit;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base audit entity class that provides common audit fields for all entities.
 * This abstract class implements audit trail functionality with automatic
 * creation and modification timestamp tracking.
 * 
 * @author Mahedee Hasan
 * @version 1.0
 * @since 2025-01-08
 */
@MappedSuperclass
public abstract class BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taskmagnet_seq")
    @SequenceGenerator(name = "taskmagnet_seq", sequenceName = "taskmagnet_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;
    
    @Column(name = "created_by", length = 50, nullable = false, updatable = false)
    private String createdBy;
    
    @Column(name = "modified_by", length = 50, nullable = false)
    private String modifiedBy;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
    
    // Default constructor
    public BaseAuditEntity() {}
    
    // Constructor with user info
    public BaseAuditEntity(String createdBy) {
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
    
    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getModifiedBy() {
        return modifiedBy;
    }
    
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    // JPA lifecycle methods for audit trail
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (modifiedDate == null) {
            modifiedDate = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }
    
    // Utility methods
    public boolean isNew() {
        return this.id == null;
    }
    
    public void softDelete(String deletedBy) {
        this.isActive = false;
        this.modifiedBy = deletedBy;
        this.modifiedDate = LocalDateTime.now();
    }
    
    public void activate(String activatedBy) {
        this.isActive = true;
        this.modifiedBy = activatedBy;
        this.modifiedDate = LocalDateTime.now();
    }
    
    // equals and hashCode based on id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseAuditEntity that = (BaseAuditEntity) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s{id=%d, createdDate=%s, modifiedDate=%s, createdBy='%s', modifiedBy='%s', isActive=%s, version=%d}",
                getClass().getSimpleName(), id, createdDate, modifiedDate, createdBy, modifiedBy, isActive, version);
    }
}
