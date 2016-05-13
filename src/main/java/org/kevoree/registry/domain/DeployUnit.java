package org.kevoree.registry.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DeployUnit.
 */
@Entity
@Table(name = "deploy_unit")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DeployUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[a-z0-9]+$")
    @Column(name = "platform", length = 50, nullable = false)
    private String platform;

    @NotNull
    @Column(name = "path", nullable = false)
    private String path;

    @ManyToOne
    private TypeDefinition typeDefinition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    public void setTypeDefinition(TypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeployUnit deployUnit = (DeployUnit) o;
        if(deployUnit.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, deployUnit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DeployUnit{" +
            "id=" + id +
            ", platform='" + platform + "'" +
            ", path='" + path + "'" +
            '}';
    }
}
