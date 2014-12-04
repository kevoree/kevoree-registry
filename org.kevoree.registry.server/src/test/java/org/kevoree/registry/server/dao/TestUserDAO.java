package org.kevoree.registry.server.dao;

import org.junit.Test;
import org.kevoree.registry.server.model.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.assertEquals;

/**
*
* Created by leiko on 03/12/14.
*/
public class TestUserDAO {

    @Test
    public void getUser() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        User u = new User();
        u.setId("kevoree@mail");
        u.setName("Kevoree");
        u.setGravatarEmail("gravatar@mail");
        u.setPassword("p@ssw0rd");
        u.setSalt("s@lt");
        UserDAO.getInstance(emf).add(u);

        User res = UserDAO.getInstance(emf).get(u.getId());
        assertEquals(u.getId(), res.getId());
        assertEquals(u.getName(), res.getName());
        assertEquals(u.getGravatarEmail(), res.getGravatarEmail());
        assertEquals(u.getPassword(), res.getPassword());
        assertEquals(u.getSalt(), res.getSalt());

        UserDAO.getInstance(emf).delete(u);
    }
}
