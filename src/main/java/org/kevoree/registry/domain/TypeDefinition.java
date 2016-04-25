package org.kevoree.registry.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TypeDefinition.
 */
@Entity
@Table(name = "type_definition")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TypeDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[A-Z]\\w*$")
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Column(name = "serialized_model", nullable = false)
    private String serializedModel;

    @NotNull
    @Min(value = 0)
    @Column(name = "version", nullable = false)
    private Long version;

    @NotNull
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[a-z]+$")
    @Column(name = "platform", length = 50, nullable = false)
    private String platform;

    @ManyToOne
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
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
            ", platform='" + platform + "'" +
            '}';
    }
}
