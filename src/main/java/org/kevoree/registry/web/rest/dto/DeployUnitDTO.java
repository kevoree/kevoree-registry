package org.kevoree.registry.web.rest.dto;

import org.kevoree.registry.domain.DeployUnit;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DeployUnit DTO
 */
public class DeployUnitDTO extends TimeableDTO {

    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @Pattern(regexp = "^([0-9]+)\\.([0-9]+)\\.([0-9]+)(?:-([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?(?:\\+[0-9A-Za-z-]+)?$")
    @NotNull
    @Size(min = 1, max = 50)
    private String version;

    @NotNull
    @Size(min = 1, max = 50)
    private String platform;

    @NotNull
    private String model;

    public DeployUnitDTO() {}

    public DeployUnitDTO(DeployUnit du) {
        super(du);
        this.id = du.getId();
        this.name = du.getName();
        this.version = du.getVersion();
        this.platform = du.getPlatform();
        this.model = du.getModel();
    }

    public DeployUnitDTO(String name, String version, String platform, String model) {
        this.name = name;
        this.version = version;
        this.platform = platform;
        this.model = model;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getPlatform() {
        return platform;
    }

    public String getModel() {
        return model;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        if (id == null) {
            return "DeployUnitDTO{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", platform='" + platform + '\'' +
                ", model='" + model + '\'' +
                '}';
        } else {
            return "DeployUnitDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", platform='" + platform + '\'' +
                ", model='" + model + '\'' +
                ", created='" + created + '\'' +
                ", modified='" + modified + '\'' +
                '}';
        }
    }
}
