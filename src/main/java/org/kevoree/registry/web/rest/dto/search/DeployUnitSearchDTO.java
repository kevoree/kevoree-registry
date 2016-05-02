package org.kevoree.registry.web.rest.dto.search;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by mleduc on 29/04/16.
 */
public class DeployUnitSearchDTO {
    @Pattern(regexp = "^\\*?[a-z0-9]*\\*?$")
    @NotNull
    @Size(min = 1, max = 52)
    private String namespace;

    @Pattern(regexp = "^\\*?[a-zA-Z0-9]*\\*?$")
    @Size(min = 1, max = 52)
    private String name;

    private Long version;

    private Boolean latest = false;

    @Pattern(regexp = "^\\*?[a-zA-Z0-9]*\\*?$")
    @Size(min = 1, max = 52)
    private String platform;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getLatest() {
        return latest;
    }

    public void setLatest(Boolean latest) {
        this.latest = latest;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
