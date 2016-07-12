package org.kevoree.registry.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TypeDefinitionDTO {

    @NotNull
    @Pattern(regexp = "^[A-Z][\\w]*$")
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    private Long version;

    @NotNull
    private String model;

    public TypeDefinitionDTO() {}

    public TypeDefinitionDTO(String name, Long version, String model) {
        this.name = name;
        this.version = version;
        this.model = model;
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
        return "TypeDefinitionDTO{" +
        "name='" + name + '\'' +
        ", version='" + version + '\'' +
        ", model='" + model + '\'' +
        '}';
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
}
