package org.kevoree.registry.server.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import org.kevoree.registry.server.util.PasswordHash;

import javax.persistence.*;
import java.security.SecureRandom;
import java.util.*;

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

    private String name;

    private String password;

    private String salt;

    @Column(name = "gravatar_email")
    private String gravatarEmail;

    @Column(name = "session_id")
    private String sessionId;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add("id", id);
        obj.add("name", name);
        obj.add("gravatarEmail", gravatarEmail);

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
