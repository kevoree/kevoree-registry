package org.kevoree.registry.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.kevoree.registry.config.Constants;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A TypeDefinition.
 */
@Entity
@Table(name = "T_NAMESPACE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Namespace implements Serializable {

    @Id
    @org.springframework.data.annotation.Id
    @Pattern(regexp = Constants.NS_NAME_REGEX)
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JsonIgnoreProperties({
        "authorities", "namespaces", "firstName", "lastName", "email", "activated", "langKey", "activationKey",
        "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate" })
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "namespaces")
    @JsonIgnore
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "namespace")
    @JsonIgnore
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
//        String membersStr = "";
//        Iterator<User> userIt = members.iterator();
//        while (userIt.hasNext()) {
//            membersStr += userIt.next().getLogin();
//            if (userIt.hasNext()) {
//                membersStr += ", ";
//            }
//        }

        return "Namespace{" +
            "name='" + name + '\'' +
            ", owner=" + owner.getLogin() +
//            ", members=[" + membersStr + "]" +
//            ", tdefs=" + tdefs +
            "}";
    }
}
