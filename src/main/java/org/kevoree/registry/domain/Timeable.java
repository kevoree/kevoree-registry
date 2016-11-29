package org.kevoree.registry.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 *
 * Created by leiko on 11/29/16.
 */
@MappedSuperclass
public abstract class Timeable {

    @NotNull
    @Column(name = "created", updatable = false)
    protected Date created = new Date();

    @NotNull
    @Version
    @Column(name = "modified")
    protected Date modified;

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
