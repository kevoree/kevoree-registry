package org.kevoree.registry.server.service;

import org.junit.Test;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.dao.UserDAO;
import org.kevoree.registry.server.exception.NotAvailableException;
import org.kevoree.registry.server.exception.NotValidException;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Set;

import static org.junit.Assert.*;

/**
*
* Created by leiko on 11/12/14.
*/
public class TestNamespaceService {

    @Test
    public void testWrongAdd() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        NamespaceService nsService = NamespaceService.getInstance(emf);
        UserService userService = UserService.getInstance(emf);
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");

        User user = UserDAO.getInstance(emf).get("foo@example.com");
        nsService.add("org.kevoree", user);

        try {
            nsService.add("org.kevoree.foo", user);
        } catch (NotAvailableException e) {
            assertNotNull(e);
        }

        try {
            nsService.add("org", user);
        } catch (NotValidException e) {
            assertNotNull(e);
        }

        try {
            nsService.add("org.kevoree", user);
        } catch (NotAvailableException e) {
            assertNotNull(e);
        }

        userService.delete(user);
    }

    @Test
    public void testAdd() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        NamespaceService nsService = NamespaceService.getInstance(emf);
        UserService userService = UserService.getInstance(emf);
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");

        User user = UserDAO.getInstance(emf).get("foo@example.com");
        nsService.add("org.kevoree", user);

        user = UserDAO.getInstance(emf).get("foo@example.com");

        assertEquals("org.kevoree", user.getNamespaces().iterator().next().getFqn());

        userService.delete(user);
    }

    @Test
    public void testJoin() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        NamespaceService nsService = NamespaceService.getInstance(emf);
        UserService userService = UserService.getInstance(emf);
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");
        userService.signin("bar@example.com", "Bar", "p@ssw0rd2");

        User owner = UserDAO.getInstance(emf).get("foo@example.com");
        User member = UserDAO.getInstance(emf).get("bar@example.com");

        nsService.add("org.kevoree", owner);
        nsService.join("org.kevoree", member);

        member = UserDAO.getInstance(emf).get("bar@example.com");
        Namespace ns = NamespaceDAO.getInstance(emf).get("org.kevoree");

        assertEquals("org.kevoree", member.getNamespaces().iterator().next().getFqn());
        assertTrue(has("foo@example.com", ns.getUsers()));
        assertTrue(has("bar@example.com", ns.getUsers()));

        userService.delete(owner);
        userService.delete(member);
    }

    @Test
    public void testLeave() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        NamespaceService nsService = NamespaceService.getInstance(emf);
        UserService userService = UserService.getInstance(emf);
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");
        userService.signin("bar@example.com", "Bar", "p@ssw0rd2");

        User owner = UserDAO.getInstance(emf).get("foo@example.com");
        User member = UserDAO.getInstance(emf).get("bar@example.com");

        nsService.add("org.kevoree", owner);
        nsService.join("org.kevoree", member);

        member = UserDAO.getInstance(emf).get("bar@example.com");
        Namespace ns = NamespaceDAO.getInstance(emf).get("org.kevoree");

        assertEquals("org.kevoree", member.getNamespaces().iterator().next().getFqn());
        assertTrue(has("foo@example.com", ns.getUsers()));
        assertTrue(has("bar@example.com", ns.getUsers()));

        nsService.leave("org.kevoree", member);

        ns = NamespaceDAO.getInstance(emf).get("org.kevoree");

        System.out.println("\n\n");
        for (User u : ns.getUsers()) {
            System.out.println("USER >>>>>>"+u.getId());
        }
        System.out.println("\n\n");

        assertTrue(has("foo@example.com", ns.getUsers()));
        assertFalse(has("bar@example.com", ns.getUsers()));

        userService.delete(owner);
        userService.delete(member);
    }

    private boolean has(String id, Set<User> users) {
        for (User ns : users) {
            if (ns.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
