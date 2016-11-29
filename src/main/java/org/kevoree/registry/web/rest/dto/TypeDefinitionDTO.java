package org.kevoree.registry.web.rest.dto;

import org.kevoree.registry.domain.TypeDefinition;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TypeDefinitionDTO extends TimeableDTO {

    private Long id;

    @NotNull
    @Pattern(regexp = "^[A-Z][\\w]*$")
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    private Long version;

    @NotNull
    private String model;

    public TypeDefinitionDTO() {}

    public TypeDefinitionDTO(TypeDefinition tdef) {
        super(tdef);
        this.id = tdef.getId();
        this.name = tdef.getName();
        this.version = tdef.getVersion();
        this.model = tdef.getModel();
    }

    public String getName() {
        return name;
    }

    public Long getVersion() {
        return version;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        if (id == null) {
            return "TypeDefinitionDTO{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", version='" + version + '\'' +
                    ", model='" + model + '\'' +
                    '}';
        } else {
            return "TypeDefinitionDTO{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", version='" + version + '\'' +
                    ", model='" + model + '\'' +
                    ", created='" + created + '\'' +
                    ", modified='" + modified + '\'' +
                    '}';
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
