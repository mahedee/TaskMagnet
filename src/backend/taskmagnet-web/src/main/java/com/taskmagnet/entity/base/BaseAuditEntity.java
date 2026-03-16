package com.taskmagnet.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseAuditEntity extends BaseEntity<Long> {

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_by", length = 100)
    private String createdByUsername;

    @Column(name = "updated_by", length = 100)
    private String updatedByUsername;

    protected BaseAuditEntity() {
        super();
    }

    protected BaseAuditEntity(String createdByUsername) {
        super();
        this.createdByUsername = createdByUsername;
        this.updatedByUsername = createdByUsername;
    }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public String getUpdatedByUsername() { return updatedByUsername; }
    public void setUpdatedByUsername(String updatedByUsername) { this.updatedByUsername = updatedByUsername; }
}
