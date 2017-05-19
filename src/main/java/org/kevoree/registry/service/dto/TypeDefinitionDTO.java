package org.kevoree.registry.service.dto;

import org.kevoree.registry.config.Constants;
import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.domain.TypeDefinition;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * Created by leiko on 4/13/17.
 */
public class TypeDefinitionDTO extends AbstractAuditingDTO {

    private Long id;

    @NotNull
    @Pattern(regexp = Constants.TDEF_NAME_REGEX)
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    private Long version;

    @NotNull
    private String model;
    private String namespace;
    private Set<Long> deployUnits = new HashSet<>();

    public TypeDefinitionDTO() {}

    public TypeDefinitionDTO(TypeDefinition tdef) {
        super(tdef);
        this.id = tdef.getId();
        this.namespace = tdef.getNamespace().getName();
        this.name = tdef.getName();
        this.version = tdef.getVersion();
        this.model = tdef.getModel();
        this.deployUnits = tdef.getDeployUnits().stream().map(DeployUnit::getId).collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getVersion() {
        return version;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getModel() {
        return model;
    }

    public Set<Long> getDeployUnits() {
        return deployUnits;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setDeployUnits(Set<Long> deployUnits) {
        this.deployUnits = deployUnits;
    }

    @Override
    public String toString() {
        return "TypeDefinitionDTO{" +
                "id=" + id + '\'' +
                ", namespace='" + namespace + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", deployUnits='" + deployUnits+ '\'' +
                "}";
    }
}
