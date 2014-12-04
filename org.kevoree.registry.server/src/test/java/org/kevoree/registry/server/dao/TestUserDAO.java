package org.kevoree.registry.server.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kevoree.registry.server.model.User;

import static org.junit.Assert.assertEquals;

/**
*
* Created by leiko on 03/12/14.
*/
public class TestUserDAO {

    private User u;

    @Before
    public void addUser() {
        u = new User();
        u.setId("kevoree@mail");
        u.setName("Kevoree");
        u.setGravatarEmail("gravatar@mail");
        u.setPassword("p@ssw0rd");
        u.setSalt("s@lt");
        UserDAO.getInstance().add(u);
    }

    @After
    public void deleteUser() {
        UserDAO.getInstance().delete(u);
    }

    @Test
    public void getUser() {
        User res = UserDAO.getInstance().get(u.getId());
        assertEquals(u.getId(), res.getId());
        assertEquals(u.getName(), res.getName());
        assertEquals(u.getGravatarEmail(), res.getGravatarEmail());
        assertEquals(u.getPassword(), res.getPassword());
        assertEquals(u.getSalt(), res.getSalt());
    }
}
