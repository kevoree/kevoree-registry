package org.kevoree.registry.server.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User POJO
 * Created by leiko on 20/11/14.
 */
@Entity
public class KevUser {

    @Id
    private String id;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name="KevUser_Namespace",
            joinColumns={
                    @JoinColumn(name="id", referencedColumnName="id", nullable = false, updatable = false)
            },
            inverseJoinColumns={
                    @JoinColumn(name="fqn", referencedColumnName="fqn", nullable = false, updatable = false)
            })
    private Set<Namespace> namespaces;

    @Column(name = "session_id")
    private String sessionId;

    private String name;

    @Column(name = "gravatar_email")
    private String gravatarEmail;

    public KevUser() {
        this.namespaces = Collections.synchronizedSet(new HashSet<Namespace>());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Namespace> getNamespaces() {
        return namespaces;
    }

    public void addNamespace(Namespace namespace) {
        this.namespaces.add(namespace);
    }

    public void removeNamespace(Namespace namespace) {
        this.namespaces.remove(namespace);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGravatarEmail() {
        return gravatarEmail;
    }

    public void setGravatarEmail(String gravatarEmail) {
        this.gravatarEmail = gravatarEmail;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add("id", id);
        obj.add("name", name);

        JsonArray namespaces = new JsonArray();
        for (Namespace ns : this.namespaces)  {
            namespaces.add(ns.toJson());
        }

        obj.add("namespaces", namespaces);
        return obj;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
