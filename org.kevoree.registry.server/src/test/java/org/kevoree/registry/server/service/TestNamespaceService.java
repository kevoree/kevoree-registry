package org.kevoree.registry.server.service;

import org.junit.Test;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.dao.UserDAO;
import org.kevoree.registry.server.exception.NotAvailableException;
import org.kevoree.registry.server.exception.NotTheOwnerException;
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
        nsService.add("org.kevoree", user.getId());

        try {
            nsService.add("org.kevoree.foo", user.getId());
        } catch (NotAvailableException e) {
            assertNotNull(e);
        }

        try {
            nsService.add("org", user.getId());
        } catch (NotValidException e) {
            assertNotNull(e);
        }

        try {
            nsService.add("org.kevoree", user.getId());
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

        // register new user
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");
        // add a namespace "org.kevoree" owned by "foo@example.com"
        nsService.add("org.kevoree", "foo@example.com");

        // retrieve the user
        User user = UserDAO.getInstance(emf).get("foo@example.com");
        // retrieve the namespace
        Namespace ns = user.getNamespaces().iterator().next();

        // assert that the user has "org.kevoree" in its namespaces
        assertEquals("org.kevoree", ns.getFqn());
        // assert that the owner of that namespace is the user
        assertTrue(nsService.isOwner("org.kevoree", user.getId()));

        // remove user => remove namespace (because the user owns the namespace)
        userService.delete(user);

        // be sure that the ns is deleted
        assertNull(NamespaceDAO.getInstance(emf).get("org.kevoree"));
    }


    @Test
    public void testRegister() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        NamespaceService nsService = NamespaceService.getInstance(emf);
        UserService userService = UserService.getInstance(emf);
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");
        userService.signin("bar@example.com", "Bar", "p@ssw0rd2");

        User owner = UserDAO.getInstance(emf).get("foo@example.com");
        User member = UserDAO.getInstance(emf).get("bar@example.com");

        nsService.add("org.kevoree", owner.getId());
        nsService.register("org.kevoree", member.getId());

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

        nsService.add("org.kevoree", owner.getId());
        nsService.register("org.kevoree", member.getId());

        member = UserDAO.getInstance(emf).get("bar@example.com");
        Namespace ns = NamespaceDAO.getInstance(emf).get("org.kevoree");

        assertEquals("org.kevoree", member.getNamespaces().iterator().next().getFqn());
        assertTrue(has("foo@example.com", ns.getUsers()));
        assertTrue(has("bar@example.com", ns.getUsers()));

        nsService.leave("org.kevoree", member);

        ns = NamespaceDAO.getInstance(emf).get("org.kevoree");

        assertTrue(has("foo@example.com", ns.getUsers()));
        assertFalse(has("bar@example.com", ns.getUsers()));

        userService.delete(owner);
        userService.delete(member);
    }

    @Test
    public void testDelete() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        NamespaceService nsService = NamespaceService.getInstance(emf);
        UserService userService = UserService.getInstance(emf);

        // register new users
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");
        userService.signin("bar@example.com", "Bar", "p@ssw0rd2");
        // add a namespace "org.kevoree" owned by "foo@example.com"
        nsService.add("org.kevoree", "foo@example.com");
        // add "bar@example.com" as a member of "org.kevoree"
        nsService.register("org.kevoree", "bar@example.com");

        // retrieve the user
        User fooUser = UserDAO.getInstance(emf).get("foo@example.com");
        User barUser = UserDAO.getInstance(emf).get("bar@example.com");
        // assert the users have "org.kevoree" in their namespaces
        assertEquals("org.kevoree", fooUser.getNamespaces().iterator().next().getFqn());
        assertEquals("org.kevoree", barUser.getNamespaces().iterator().next().getFqn());

        // remove namespace
        nsService.delete(NamespaceDAO.getInstance(emf).get("org.kevoree"));

        // be sure that the ns is deleted
        assertNull(NamespaceDAO.getInstance(emf).get("org.kevoree"));

        // be sure that no user is a member of the deleted namespace
        fooUser = UserDAO.getInstance(emf).get("foo@example.com");
        assertTrue(fooUser.getNamespaces().isEmpty());
        barUser = UserDAO.getInstance(emf).get("bar@example.com");
        assertTrue(barUser.getNamespaces().isEmpty());
    }

    @Test
    public void testDeleteWithFQNAndUserID() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        NamespaceService nsService = NamespaceService.getInstance(emf);
        UserService userService = UserService.getInstance(emf);

        // register new users
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");
        userService.signin("bar@example.com", "Bar", "p@ssw0rd2");
        // add a namespace "org.kevoree" owned by "foo@example.com"
        nsService.add("org.kevoree", "foo@example.com");
        // add "bar@example.com" as a member of "org.kevoree"
        nsService.register("org.kevoree", "bar@example.com");

        // retrieve the user
        User fooUser = UserDAO.getInstance(emf).get("foo@example.com");
        User barUser = UserDAO.getInstance(emf).get("bar@example.com");
        // assert the users have "org.kevoree" in their namespaces
        assertEquals("org.kevoree", fooUser.getNamespaces().iterator().next().getFqn());
        assertEquals("org.kevoree", barUser.getNamespaces().iterator().next().getFqn());

        // remove namespace
        nsService.delete("org.kevoree", "foo@example.com");

        // be sure that the ns is deleted
        assertNull(NamespaceDAO.getInstance(emf).get("org.kevoree"));

        // be sure that no user is a member of the deleted namespace
        fooUser = UserDAO.getInstance(emf).get("foo@example.com");
        assertTrue(fooUser.getNamespaces().isEmpty());
        barUser = UserDAO.getInstance(emf).get("bar@example.com");
        assertTrue(barUser.getNamespaces().isEmpty());
    }

    @Test
    public void testWrongDeleteWithFQNAndUserID() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        NamespaceService nsService = NamespaceService.getInstance(emf);
        UserService userService = UserService.getInstance(emf);

        // register new users
        userService.signin("foo@example.com", "Foo", "p@ssw0rd");
        userService.signin("bar@example.com", "Bar", "p@ssw0rd2");
        // add a namespace "org.kevoree" owned by "foo@example.com"
        nsService.add("org.kevoree", "foo@example.com");
        // add "bar@example.com" as a member of "org.kevoree"
        nsService.register("org.kevoree", "bar@example.com");

        // retrieve the user
        User fooUser = UserDAO.getInstance(emf).get("foo@example.com");
        User barUser = UserDAO.getInstance(emf).get("bar@example.com");
        // assert the users have "org.kevoree" in their namespaces
        assertEquals("org.kevoree", fooUser.getNamespaces().iterator().next().getFqn());
        assertEquals("org.kevoree", barUser.getNamespaces().iterator().next().getFqn());

        // remove namespace using wrong owner id
        try {
            nsService.delete("org.kevoree", "bar@example.com");
        } catch (NotTheOwnerException e) {
            assertNotNull(e);
        }

        // be sure that the ns is NOT deleted
        assertNotNull(NamespaceDAO.getInstance(emf).get("org.kevoree"));

        // be sure that no user is STILL a member of the namespace
        fooUser = UserDAO.getInstance(emf).get("foo@example.com");
        assertFalse(fooUser.getNamespaces().isEmpty());
        barUser = UserDAO.getInstance(emf).get("bar@example.com");
        assertFalse(barUser.getNamespaces().isEmpty());

        // clean the users for further tests
        userService.delete("foo@example.com");
        userService.delete("bar@example.com");
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