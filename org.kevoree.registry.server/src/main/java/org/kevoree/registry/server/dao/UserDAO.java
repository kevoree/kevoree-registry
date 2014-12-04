package org.kevoree.registry.server.dao;

import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.HashSet;

/**
 * Created by leiko on 20/11/14.
 */
public class UserDAO extends AbstractDAO<User> {

    public static UserDAO INSTANCE;

    private UserDAO() {
        super(User.class);
    }

    public static UserDAO getInstance() {
        if (UserDAO.INSTANCE == null) {
            UserDAO.INSTANCE = new UserDAO();
        }
        return UserDAO.INSTANCE;
    }

    @Override
    public void delete(User user) {
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {
            tx = em.getTransaction();
            tx.begin();

            user = em.find(user.getClass(), user.getId());

            for (Namespace ns : user.getNamespaces()) {
                ns = em.find(ns.getClass(), ns.getFqn());
                if (ns.getOwner() != null && ns.getOwner().getId().equals(user.getId())) {
                    // delete namespace too
                    NamespaceDAO.getInstance().delete(ns);
                } else {
                    // just leave namespace
                    ns.removeUser(user);
                    em.merge(ns);
                }
            }

            em.remove(user);

            tx.commit();

        } catch (RuntimeException e) {
            if ( tx != null && tx.isActive() ) { tx.rollback(); }
            throw e;
        } finally {
            em.close();
        }
    }

    public void leave(User user, Namespace ns) {
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {
            tx = em.getTransaction();
            tx.begin();
            user = em.find(user.getClass(), user.getId());
            ns = em.find(ns.getClass(), ns.getFqn());
            user.removeNamespace(ns);
            ns.removeUser(user);
            em.merge(ns);
            em.merge(user);
            tx.commit();

        } catch (RuntimeException e) {
            if ( tx != null && tx.isActive() ) { tx.rollback(); }
            throw e;
        } finally {
            em.close();
        }
    }
}
