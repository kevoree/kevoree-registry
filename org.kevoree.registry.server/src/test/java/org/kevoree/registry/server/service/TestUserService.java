package org.kevoree.registry.server.service;

import org.junit.Test;
import org.kevoree.registry.server.dao.UserDAO;
import org.kevoree.registry.server.exception.NotAvailableException;
import org.kevoree.registry.server.exception.PasswordException;
import org.kevoree.registry.server.model.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertEquals;

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
}
