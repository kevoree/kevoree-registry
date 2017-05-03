package org.kevoree.registry.service.dto;

import org.kevoree.registry.domain.AbstractAuditingEntity;

import java.time.ZonedDateTime;

/**
 *
 * Created by leiko on 11/29/16.
 */
abstract class AbstractAuditingDTO {

    private ZonedDateTime createdDate;
    private String createdBy;
    private String lastModifiedBy;
    private ZonedDateTime lastModifiedDate;

    AbstractAuditingDTO() {}

    AbstractAuditingDTO(AbstractAuditingEntity audit) {
        this.createdDate = audit.getCreatedDate();
        this.createdBy = audit.getCreatedBy();
        this.lastModifiedBy = audit.getLastModifiedBy();
        this.lastModifiedDate = audit.getLastModifiedDate();
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
