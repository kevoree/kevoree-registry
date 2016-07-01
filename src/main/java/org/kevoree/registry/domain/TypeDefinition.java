package org.kevoree.registry.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public class TypeDefinition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[A-Z][\\w]*$")
    @Size(min = 1, max = 50)
    @Column(length = 50)
    private String name;

    @NotNull
    @Pattern(regexp = "^([0-9]+)\\.([0-9]+)\\.([0-9]+)(?:-([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?(?:\\+[0-9A-Za-z-]+)?$")
    @Size(min = 1, max = 50)
    @Column(length = 50)
    private String version;

    @ManyToOne
    @JsonIgnoreProperties({ "members", "typeDefinitions" })
    private Namespace namespace;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "typeDefinition")
    @JsonIgnoreProperties({ "typeDefinition" })
    private Set<DeployUnit> deployUnits = new HashSet<>();

    private Long nbDownloads = 0L;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getNbDownloads() {
        return nbDownloads;
    }

    public void setNbDownloads(Long nbDownloads) {
        this.nbDownloads = nbDownloads;
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
            ", nbDownloads=" + nbDownloads +
            ", deployUnits=" + deployUnits +
            "}";
    }
}
