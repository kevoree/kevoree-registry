package org.kevoree.registry.server.model;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User POJO
 * Created by leiko on 20/11/14.
 */
@Entity
@Table(name = "KevUser")
public class User {

    @Id
    private String id;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(name="KevUser_Namespace")
    private Set<Namespace> namespaces;

    private String name;

    private String password;

    private String salt;

    @Column(name = "gravatar_email")
    private String gravatarEmail;

    public User() {
        this.namespaces = new HashSet<Namespace>();
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

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add("id", id);
        obj.add("name", name);
        obj.add("gravatarEmail", gravatarEmail);
        obj.add("oauthOnly", salt == null);

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return this.id.equals(((User) obj).id);
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
