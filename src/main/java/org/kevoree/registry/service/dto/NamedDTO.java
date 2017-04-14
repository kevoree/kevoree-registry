package org.kevoree.registry.service.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class NamedDTO {

    @Pattern(regexp = "^[a-z0-9]+(\\.[a-z0-9]+)*$")
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    public NamedDTO() {
    }

    public NamedDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "NamedDTO{" +
        "name='" + name + '\'' + '}';
    }
}
