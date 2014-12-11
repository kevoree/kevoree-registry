package org.kevoree.registry.server.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Namespace POJO
 * Created by leiko on 20/11/14.
 */
@Entity
public class Namespace {

    @Id
    private String fqn;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy="namespaces", cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    private Set<User> users;

    @ManyToOne(optional = false)
    private User owner;

    public Namespace() {
        this.users = new HashSet<User>();
    }

    public Set<User> getUsers() {
        return users;
    }

    public void addUser(User u) {
        this.users.add(u);
    }

    public void removeUser(User u) {
        this.users.remove(u);
    }

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

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add("fqn", fqn);

        JsonArray users = new JsonArray();
        for (User u : this.users) {
            users.add(u.getId());
        }

        obj.add("users", users);
        obj.add("owner", owner == null ? null : owner.getId());
        return obj;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Namespace) {
            return this.fqn.equals(((Namespace) obj).fqn);
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return fqn.hashCode();
    }
}
