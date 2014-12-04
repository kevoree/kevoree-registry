package org.kevoree.registry.server.dao;

import org.junit.Test;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.assertEquals;

/**
*
* Created by leiko on 03/12/14.
*/
public class TestNamespaceDAO {

    @Test
    public void addNewMemberToNamespace() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");

        // owner
        User owner = new User();
        owner.setId("owner");
        UserDAO.getInstance(emf).add(owner);

        // namespace
        Namespace ns = new Namespace();
        ns.setFqn("org.kevoree");
        // define user "owner" as owner
        ns.setOwner(owner);
        NamespaceDAO.getInstance(emf).add(ns);

        // newMember
        User newMember = new User();
        newMember.setId("newMember");
        UserDAO.getInstance(emf).add(newMember);

        owner.addNamespace(ns);
        UserDAO.getInstance(emf).update(owner);

        // retrieve objects from db
        newMember = UserDAO.getInstance(emf).get(newMember.getId());
        ns = NamespaceDAO.getInstance(emf).get(ns.getFqn());
        // add newMember to ns
        newMember.addNamespace(ns);
        UserDAO.getInstance(emf).update(newMember);

        // retrieve namespace
        Namespace res = NamespaceDAO.getInstance(emf).get(ns.getFqn());

        // asserts
        assertEquals(ns.getFqn(), res.getFqn());
        assertEquals(owner.getId(), res.getOwner().getId());

        // clean both
        UserDAO.getInstance(emf).delete(owner);
        UserDAO.getInstance(emf).delete(newMember);
    }
}
