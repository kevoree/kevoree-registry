package org.kevoree.registry.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class TypeDefinitionDTO {

    @Pattern(regexp = "^[A-Z][\\w]*$")
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @Pattern(regexp = "^\\bv?(?:0|[1-9][0-9]*)\\.(?:0|[1-9][0-9]*)\\.(?:0|[1-9][0-9]*)(?:-[\\da-z\\-]+(?:\\.[\\da-z\\-]+)*)?(?:\\+[\\da-z\\-]+(?:\\.[\\da-z\\-]+)*)?\\b$")
    @NotNull
    @Size(min = 6, max = 50)
    private String version;

    @Size(max = 50)
    @NotNull
    private String serializedModel;

    @Pattern(regexp = "^[a-z0-9]*$")
    @Size(max = 50)
    @NotNull
    private String namespace;

    public TypeDefinitionDTO() {}

    public TypeDefinitionDTO(String namespace, String name, String version, String serializedModel) {
        this.namespace = namespace;
        this.name = name;
        this.version = version;
        this.serializedModel = serializedModel;
    }


    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getSerializedModel() {
        return serializedModel;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public String toString() {
        return "TypeDefinitionDTO{" +
        "namespace='" + namespace + '\'' +
        ", name='" + name + '\'' +
        ", version='" + version + '\'' +
        ", serializedModel='" + serializedModel + '\'' +
        '}';
    }
}
