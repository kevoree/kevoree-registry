package org.kevoree.registry.web.rest.vm;

import org.kevoree.registry.config.Constants;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * Created by leiko on 1/26/17.
 */
public class NamespaceDetailVM {

    @Pattern(regexp = Constants.NS_NAME_REGEX)
    @Size(min = 1, max = 50)
    private String name;

    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String owner;

    private Set<String> members = new HashSet<>();

    public NamespaceDetailVM() {}

    public NamespaceDetailVM(Namespace ns) {
        this(ns.getName(), ns.getOwner().getLogin(),
                ns.getMembers().stream().map(User::getLogin).collect(Collectors.toSet()));
    }

    public NamespaceDetailVM(String name, String owner, Set<String> members) {
        this.name = name;
        this.owner = owner;
        this.members = members;
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
                "}";
    }
}
