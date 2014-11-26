package org.kevoree.registry.server.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by leiko on 20/11/14.
 */
@Entity
public class Namespace {

    @Id
    private String fqn;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy="namespaces", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    private Set<KevUser> users;

    @OneToOne(targetEntity = KevUser.class)
    private KevUser owner;

    public Namespace() {
        this.users = Collections.synchronizedSet(new HashSet<KevUser>());
    }

    public Set<KevUser> getUsers() {
        return users;
    }

    public void addUser(KevUser u) {
        this.users.add(u);
    }

    public void removeUser(KevUser u) {
        this.users.remove(u);
    }

    public String getFqn() {
        return fqn;
    }

    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    public KevUser getOwner() {
        return owner;
    }

    public void setOwner(KevUser owner) {
        this.owner = owner;
    }

    public void setUsers(Set<KevUser> users) {
        this.users = users;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add("fqn", fqn);

        JsonArray users = new JsonArray();
        for (KevUser u : this.users) {
            users.add(u.getId());
        }

        obj.add("users", users);
        obj.add("owner", owner.getId());
        return obj;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
