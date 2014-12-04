package org.kevoree.registry.server.dao;

import org.junit.Test;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
*
* Created by leiko on 03/12/14.
*/
public class TestUserNamespaceDAO {

    @Test
    public void deleteOwner() {
        User u = new User();
        u.setId("kevoree@mail");
        u.setName("Kevoree");
        u.setGravatarEmail("gravatar@mail");
        u.setPassword("p@ssw0rd");
        u.setSalt("s@lt");
        UserDAO.getInstance().add(u);

        Namespace n = new Namespace();
        n.setFqn("org.kevoree");
        n.setOwner(u);
        NamespaceDAO.getInstance().add(n);

        u = UserDAO.getInstance().get(u.getId());
        u.addNamespace(n);
        UserDAO.getInstance().update(u);

        User newUser = new User();
        newUser.setId("new.user@mail");
        newUser.setName("New guys");
        newUser.setGravatarEmail("gravatar@newguy");
        newUser.setPassword("bloop");
        newUser.setSalt("bleep");
        UserDAO.getInstance().add(newUser);

        // add newUser to namespace
        newUser = UserDAO.getInstance().get(newUser.getId());
        newUser.addNamespace(n);
        UserDAO.getInstance().update(newUser);

        // delete owner of org.kevoree namespace should make newUser leave it too
        UserDAO.getInstance().delete(u);

        // newUser should be member of 0 namespace because "n" has been removed
        newUser = UserDAO.getInstance().get(newUser.getId());
        Set<Namespace> namespaces = newUser.getNamespaces();
        assertEquals(0, namespaces.size());

        // clean
        UserDAO.getInstance().delete(newUser);
    }
}
