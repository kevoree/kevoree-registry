package org.kevoree.registry.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
 * A Namespace.
 */
@Entity
@Table(name = "T_NAMESPACE")
@JsonSerialize(using = CustomNamespaceSerializer.class)
@JsonDeserialize(using = CustomNamespaceDeserializer.class)
public class Namespace implements Serializable {

    @Id
    @Pattern(regexp = "^([a-z_]{2,}(\\.[a-z_]+[0-9]*[a-z_]*)(\\.[a-z_]+[0-9]*[a-z_]*)*)$")
    @Size(min = 1, max = 75)
    @Column(length = 75)
    private String fqn;

    @ManyToOne
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "namespaces")
    private Set<User> members = new HashSet<>();

    public String getFqn() {
        return fqn;
    }

    public void setFqn(String fqn) {
        this.fqn = fqn;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Namespace namespace = (Namespace) o;

        if (fqn != null ? !fqn.equals(namespace.fqn) : namespace.fqn != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fqn != null ? fqn.hashCode() : 0;
    }

    @Override
    public String toString() {
        String membersStr = "";
        Iterator<User> it = members.iterator();
        while (it.hasNext()) {
            membersStr += it.next().getLogin();
            if (it.hasNext()) {
                membersStr += ", ";
            }
        }

        String str = "Namespace{" +
            "fqn='" + fqn + "'";

        if (owner != null) {
            str += ", owner='" + owner.getLogin() + "'";
        }

        str += ", members=[" + membersStr + "]" +
            '}';

        return str;
    }
}
