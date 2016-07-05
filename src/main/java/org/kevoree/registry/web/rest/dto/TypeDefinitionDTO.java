package org.kevoree.registry.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TypeDefinitionDTO {

    @Pattern(regexp = "^[A-Z][\\w]*$")
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @Pattern(regexp = "^([0-9]+)\\.([0-9]+)\\.([0-9]+)(?:-([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?(?:\\+[0-9A-Za-z-]+)?$")
    @NotNull
    @Size(min = 1, max = 50)
    private String version;

    @NotNull
    private String model;

    public TypeDefinitionDTO() {}

    public TypeDefinitionDTO(String name, String version, String model) {
        this.name = name;
        this.version = version;
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
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

    public void setVersion(String version) {
        this.version = version;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
