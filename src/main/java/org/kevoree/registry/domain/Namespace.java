package org.kevoree.registry.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.kevoree.registry.domain.util.CustomNamespaceDeserializer;
import org.kevoree.registry.domain.util.CustomNamespaceSerializer;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A TypeDefinition.
 */
@Entity
@Table(name = "T_NAMESPACE")
@JsonSerialize(using = CustomNamespaceSerializer.class)
@JsonDeserialize(using = CustomNamespaceDeserializer.class)
public class Namespace implements Serializable {

    @Id
    @Pattern(regexp = "^[a-z0-9]*$")
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String name;

    @ManyToOne
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "namespaces")
    private Set<User> members = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "T_NAMESPACE_TYPE_DEFINITION",
        joinColumns = {@JoinColumn(name = "ns_name", referencedColumnName = "name")},
        inverseJoinColumns = {
            @JoinColumn(name = "tdef_name", referencedColumnName = "name"),
            @JoinColumn(name = "tdef_version", referencedColumnName = "version")
        })
    private Set<TypeDefinition> tdefs = new HashSet<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public void addMember(User member) {
        this.members.add(member);
    }

    public void removeMember(User member) {
        this.members.remove(member);
    }

    public Set<TypeDefinition> getTypeDefinitions() {
        return tdefs;
    }

    public void setTypeDefinitions(Set<TypeDefinition> tdefs) {
        this.tdefs = tdefs;
    }

    public void addTypeDefinition(TypeDefinition tdef) {
        this.tdefs.add(tdef);
    }

    public void removeTypeDefinition(TypeDefinition tdef) {
        this.tdefs.remove(tdef);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Namespace ns = (Namespace) o;

        return !(name != null && !name.equals(ns.name));

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        String membersStr = "";
        Iterator<User> userIt = members.iterator();
        while (userIt.hasNext()) {
            membersStr += userIt.next().getLogin();
            if (userIt.hasNext()) {
                membersStr += ", ";
            }
        }

        return "Namespace{" +
            "name='" + name + '\'' +
            ", owner=" + owner.getLogin() +
            ", members=[" + membersStr + "]" +
            ", tdefs=" + tdefs +
            "}";
    }
}
