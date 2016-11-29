package org.kevoree.registry.web.rest.dto;

import org.kevoree.registry.domain.Timeable;

import java.util.Date;

/**
 *
 * Created by leiko on 11/29/16.
 */
abstract class TimeableDTO {

    protected Date created;
    protected Date modified;

    TimeableDTO() {}

    TimeableDTO(Timeable timeable) {
        this.created = timeable.getCreated();
        this.modified = timeable.getModified();
    }

    public Date getModified() {
        return modified;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
