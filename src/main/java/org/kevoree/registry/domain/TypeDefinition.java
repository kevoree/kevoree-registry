package org.kevoree.registry.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A TypeDefinition.
 */
@Entity
@Table(name = "T_TYPE_DEFINITION")
public class TypeDefinition extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[A-Z][\\w]*$")
    @Size(min = 1, max = 50)
    @Column(length = 50)
    private String name;

    @NotNull
    private Long version;

    @ManyToOne
    @JsonIgnoreProperties({ "typeDefinitions" })
    private Namespace namespace;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "typeDefinition")
    @JsonIgnoreProperties({ "typeDefinition" })
    private Set<DeployUnit> deployUnits = new HashSet<>();

    @NotNull
    @Type(type="org.hibernate.type.StringClobType")
    @Column(name = "model")
    private String model;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace ns) {
        this.namespace = ns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Set<DeployUnit> getDeployUnits() {
        return deployUnits;
    }

    public void setDeployUnits(Set<DeployUnit> deployUnits) {
        this.deployUnits = deployUnits;
    }

    public void addDeployUnit(DeployUnit deployUnit) {
        this.deployUnits.add(deployUnit);
    }

    public void removeDeployUnit(DeployUnit deployUnit) {
        this.deployUnits.remove(deployUnit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TypeDefinition model = (TypeDefinition) o;

        return !(id != null && !id.equals(model.id));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Model{" +
            "id='" + id + "'" +
            ", name='" + name + "'" +
            ", version='" + version + "'" +
            ", namespace='" + namespace.getName() + '\'' +
            ", deployUnits=" + deployUnits +
            ", model='" + model + '\'' +
            ", createdBy='" + getCreatedBy() + '\'' +
            ", createdDate='" + getCreatedDate()+ '\'' +
            ", lastModifiedBy='" + getLastModifiedBy()+ '\'' +
            ", lastModifiedDate='" + getLastModifiedDate()+ '\'' +
            "}";
    }
}
