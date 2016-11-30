package org.kevoree.registry.web.rest.dto;

import org.joda.time.DateTime;
import org.kevoree.registry.domain.AbstractAuditingEntity;

/**
 *
 * Created by leiko on 11/29/16.
 */
abstract class AbstractAuditingDTO {

    private String createdBy;
    private String lastModifiedBy;
    private DateTime lastModifiedDate;

    AbstractAuditingDTO() {}

    AbstractAuditingDTO(AbstractAuditingEntity audit) {
        this.createdBy = audit.getCreatedBy();
        this.lastModifiedBy = audit.getLastModifiedBy();
        this.lastModifiedDate = audit.getLastModifiedDate();
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

    public DateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
