package org.kevoree.registry.server.dao;

import org.junit.Test;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import static org.junit.Assert.assertEquals;

/**
*
* Created by leiko on 03/12/14.
*/
public class TestNamespaceDAO {

    @Test
    public void addNewMemberToNamespace() {
        // owner
        User owner = new User();
        owner.setId("owner");
        UserDAO.getInstance().add(owner);

        // namespace
        Namespace ns = new Namespace();
        ns.setFqn("org.kevoree");
        // define user "owner" as owner
        ns.setOwner(owner);
        NamespaceDAO.getInstance().add(ns);

        // newMember
        User newMember = new User();
        newMember.setId("newMember");
        UserDAO.getInstance().add(newMember);

        owner.addNamespace(ns);
        UserDAO.getInstance().update(owner);

        // retrieve objects from db
        newMember = UserDAO.getInstance().get(newMember.getId());
        ns = NamespaceDAO.getInstance().get(ns.getFqn());
        // add newMember to ns
        newMember.addNamespace(ns);
        UserDAO.getInstance().update(newMember);

        // retrieve namespace
        Namespace res = NamespaceDAO.getInstance().get(ns.getFqn());

        // asserts
        assertEquals(ns.getFqn(), res.getFqn());
        assertEquals(owner.getId(), res.getOwner().getId());

        // clean both
        UserDAO.getInstance().delete(owner);
        UserDAO.getInstance().delete(newMember);
    }
}
