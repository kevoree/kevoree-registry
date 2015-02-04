package org.kevoree.registry.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.kevoree.registry.domain.util.CustomNamespaceDeserializer;
import org.kevoree.registry.domain.util.CustomNamespaceSerializer;
import org.kevoree.registry.domain.util.CustomTypeDefinitionDeserializer;
import org.kevoree.registry.domain.util.CustomTypeDefinitionSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A TypeDefinition.
 */
@Entity
@Table(name = "T_TYPE_DEFINITION")
@JsonSerialize(using = CustomTypeDefinitionSerializer.class)
@JsonDeserialize(using = CustomTypeDefinitionDeserializer.class)
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
    @Pattern(regexp = "^\\bv?(?:0|[1-9][0-9]*)\\.(?:0|[1-9][0-9]*)\\.(?:0|[1-9][0-9]*)(?:-[\\da-z\\-]+(?:\\.[\\da-z\\-]+)*)?(?:\\+[\\da-z\\-]+(?:\\.[\\da-z\\-]+)*)?\\b$")
    @Size(min = 1, max = 50)
    @Column(length = 50)
    private String version;

    @ManyToOne
    private Namespace namespace;

    @NotNull
    @Type(type="org.hibernate.type.StringClobType")
    private String serializedModel;

    private Long nbDownloads = 0L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerializedModel() {
        return serializedModel;
    }

    public void setSerializedModel(String serializedModel) {
        this.serializedModel = serializedModel;
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
            "}";
    }
}
