package org.kevoree.registry.service.dto;

import org.kevoree.registry.config.Constants;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * A DTO representing a Namespace with its owner and members name only
 */
public class NamespaceDTO extends AbstractAuditingDTO {

    @Pattern(regexp = Constants.NS_NAME_REGEX)
    @Size(min = 1, max = 50)
    private String name;

    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String owner;

    private Set<String> members = new HashSet<>();
    private Set<Long> typeDefinitions = new HashSet<>();

    public NamespaceDTO() {}

    public NamespaceDTO(Namespace ns) {
        super(ns);
        this.name = ns.getName();
        this.owner = ns.getOwner().getLogin();
        this.members = ns.getMembers().stream().map(User::getLogin).collect(Collectors.toSet());
        this.typeDefinitions = ns.getTypeDefinitions().stream().map(TypeDefinition::getId).collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public Set<String> getMembers() {
        return members;
    }

    public Set<Long> getTypeDefinitions() {
        return typeDefinitions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "NamespaceDTO{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", members='" + members + '\'' +
                ", typeDefinitions='" + typeDefinitions + '\'' +
                "}";
    }
}
