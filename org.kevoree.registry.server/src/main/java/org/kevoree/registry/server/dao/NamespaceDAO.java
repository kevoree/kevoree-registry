package org.kevoree.registry.server.dao;

import org.kevoree.registry.server.model.User;
import org.kevoree.registry.server.model.Namespace;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;

/**
 *
 * Created by leiko on 20/11/14.
 */
public class NamespaceDAO extends AbstractDAO<Namespace> {

    public static NamespaceDAO INSTANCE;

    private NamespaceDAO() {
        super(Namespace.class);
    }

    public static NamespaceDAO getInstance() {
        if (NamespaceDAO.INSTANCE == null) {
            NamespaceDAO.INSTANCE = new NamespaceDAO();
        }
        return NamespaceDAO.INSTANCE;
    }

    @Override
    public void delete(Namespace ns) {
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {
            tx = em.getTransaction();
            tx.begin();
            ns = em.find(ns.getClass(), ns.getFqn());
            for (User u : new HashSet<User>(ns.getUsers())) {
                // update join
                ns.removeUser(u);
                u.removeNamespace(ns);
                em.merge(u);
            }
            em.remove(ns);
            tx.commit();

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
