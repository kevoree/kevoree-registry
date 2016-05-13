package org.kevoree.registry.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TypeDefinition.
 */
@Entity
@Table(name = "type_definition", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "version", "namespace_id" }))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TypeDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "serialized_model", nullable = false)
    private String serializedModel;

    @Column(name = "version", nullable = false)
    private Long version;

    @ManyToOne
    @JoinColumn(name="namespace_id")
    private Namespace namespace;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerializedModel() {
        return serializedModel;
    }

    public void setSerializedModel(String serializedModel) {
        this.serializedModel = serializedModel;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeDefinition typeDefinition = (TypeDefinition) o;
        if(typeDefinition.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, typeDefinition.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TypeDefinition{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", serializedModel='" + serializedModel + "'" +
            ", version='" + version + "'" +
            '}';
    }
}
