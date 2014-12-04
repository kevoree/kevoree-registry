package org.kevoree.registry.server.dao;

import org.junit.Test;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
*
* Created by leiko on 03/12/14.
*/
public class TestUserNamespaceDAO {

    @Test
    public void deleteOwner() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        User u = new User();
        u.setId("kevoree@mail");
        u.setName("Kevoree");
        u.setGravatarEmail("gravatar@mail");
        u.setPassword("p@ssw0rd");
        u.setSalt("s@lt");
        UserDAO.getInstance(emf).add(u);

        Namespace n = new Namespace();
        n.setFqn("org.kevoree");
        n.setOwner(u);
        NamespaceDAO.getInstance(emf).add(n);

        u = UserDAO.getInstance(emf).get(u.getId());
        u.addNamespace(n);
        UserDAO.getInstance(emf).update(u);

        User newUser = new User();
        newUser.setId("new.user@mail");
        newUser.setName("New guys");
        newUser.setGravatarEmail("gravatar@newguy");
        newUser.setPassword("bloop");
        newUser.setSalt("bleep");
        UserDAO.getInstance(emf).add(newUser);

        // add newUser to namespace
        newUser = UserDAO.getInstance(emf).get(newUser.getId());
        newUser.addNamespace(n);
        UserDAO.getInstance(emf).update(newUser);

        // delete owner of org.kevoree namespace should make newUser leave it too
        UserDAO.getInstance(emf).delete(u);

        // newUser should be member of 0 namespace because "n" has been removed
        newUser = UserDAO.getInstance(emf).get(newUser.getId());
        Set<Namespace> namespaces = newUser.getNamespaces();
        assertEquals(0, namespaces.size());

        // clean
        UserDAO.getInstance(emf).delete(newUser);
    }
}
