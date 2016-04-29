package org.kevoree.registry.web.rest.dto.search;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by mleduc on 28/04/16.
 */
public class NamespaceSearchDTO {
    @Pattern(regexp = "^\\*?[a-z0-9]*\\*?$")
    @NotNull
    @Size(min = 1, max = 52)
    private String namespace;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
