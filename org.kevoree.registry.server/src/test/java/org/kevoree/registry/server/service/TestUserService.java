package org.kevoree.registry.server.service;

import org.junit.Test;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.dao.UserDAO;
import org.kevoree.registry.server.exception.NotAvailableException;
import org.kevoree.registry.server.exception.PasswordException;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
*
* Created by leiko on 11/12/14.
*/
public class TestUserService {

    @Test
    public void testAdd() throws PasswordException, InvalidKeySpecException, NoSuchAlgorithmException, NotAvailableException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        UserService userService = UserService.getInstance(emf);
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");

        User user = UserDAO.getInstance(emf).get("foo@example.com");

        assertEquals("foo@example.com", user.getId());
        assertEquals("Foo", user.getName());

        UserService.getInstance(emf).delete(user);
    }

    @Test
    public void testCascadeDelete() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        NamespaceService nsService = NamespaceService.getInstance(emf);
        UserService userService = UserService.getInstance(emf);

        // register new user
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");
        // add a namespace "org.kevoree" owned by "foo@example.com"
        nsService.add("org.kevoree", "foo@example.com");
        // add a namespace "fr.braindead" owned by "foo@example.com"
        nsService.add("fr.braindead", "foo@example.com");

        // retrieve the user
        User user = UserDAO.getInstance(emf).get("foo@example.com");

        // assert that the user has "org.kevoree" in its namespaces
        assertTrue(has(user.getNamespaces(), "org.kevoree"));
        // assert that the user has "fr.braindead" in its namespaces
        assertTrue(has(user.getNamespaces(), "fr.braindead"));

        // assert that the owner of those namespaces is the user
        assertTrue(nsService.isOwner("org.kevoree", user.getId()));
        assertTrue(nsService.isOwner("fr.braindead", user.getId()));

        // remove user => remove namespaces (because the user owns the namespace)
        userService.delete(user);

        // be sure that the namespaces are deleted
        assertNull(NamespaceDAO.getInstance(emf).get("org.kevoree"));
        assertNull(NamespaceDAO.getInstance(emf).get("fr.braindead"));
    }

    private boolean has(Set<Namespace> namespaces, String fqn) {
        for (Namespace ns : namespaces) {
            if (ns.getFqn().equals(fqn)) {
                return true;
            }
        }
        return false;
    }
}
