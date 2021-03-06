package org.kevoree.registry.service.dto;

import org.kevoree.registry.domain.DeployUnit;

import javax.validation.constraints.NotNull;

/**
 *
 * Created by leiko on 4/13/17.
 */
public class DeployUnitDTO extends AbstractAuditingDTO {

    private Long id;
    private String namespace;
    private Long tdefId;
    private String tdefName;
    private Long tdefVersion;

    @NotNull
    private String name;
    @NotNull
    private String version;
    @NotNull
    private String platform;
    @NotNull
    private String model;

    public DeployUnitDTO() {}

    public DeployUnitDTO(DeployUnit du) {
        super(du);
        this.id = du.getId();
        this.namespace = du.getTypeDefinition().getNamespace().getName();
        this.tdefId = du.getTypeDefinition().getId();
        this.tdefName = du.getTypeDefinition().getName();
        this.tdefVersion = du.getTypeDefinition().getVersion();
        this.name = du.getName();
        this.version = du.getVersion();
        this.model = du.getModel();
        this.platform = du.getPlatform();
    }

    public Long getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
    }

    public Long getTdefId() {
        return tdefId;
    }

    public String getTdefName() {
        return tdefName;
    }

    public Long getTdefVersion() {
        return tdefVersion;
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

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setTdefId(Long tdefId) {
        this.tdefId = tdefId;
    }

    public void setTdefName(String tdefName) {
        this.tdefName = tdefName;
    }

    public void setTdefVersion(Long tdefVersion) {
        this.tdefVersion = tdefVersion;
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
        return "DeployUnitDTO{" +
                "id=" + id + '\'' +
                ", namespace='" + namespace + '\'' +
                ", tdefId='" + tdefId + '\'' +
                ", tdefName='" + tdefName + '\'' +
                ", tdefVersion='" + tdefVersion + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", platform='" + platform + '\'' +
                "}";
    }
}
