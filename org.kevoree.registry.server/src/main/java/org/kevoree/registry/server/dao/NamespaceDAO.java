package org.kevoree.registry.server.dao;

import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * Created by leiko on 20/11/14.
 */
public class NamespaceDAO extends AbstractDAO<Namespace> {

    public static NamespaceDAO INSTANCE;

    private NamespaceDAO(EntityManagerFactory emf) {
        super(emf, Namespace.class);
    }

    public static NamespaceDAO getInstance(EntityManagerFactory emf) {
        if (NamespaceDAO.INSTANCE == null) {
            NamespaceDAO.INSTANCE = new NamespaceDAO(emf);
        }
        return NamespaceDAO.INSTANCE;
    }

    public void add(Namespace ns) {
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(ns);
            tx.commit();

        } catch (RuntimeException e) {
            if ( tx != null && tx.isActive() ) { tx.rollback(); }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Namespace> findStartsWith(String fqn) {
        List<Namespace> namespaces = new ArrayList<Namespace>();
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {
            tx = em.getTransaction();
            tx.begin();
            TypedQuery<Namespace> query = em.createQuery("SELECT n FROM " +
                    Namespace.class.getSimpleName() +
                    " n WHERE n.fqn LIKE :fqn", Namespace.class);
            query.setParameter("fqn", fqn + ".%");
            try {
                namespaces = query.getResultList();
            } catch (Exception e) {
                namespaces = null;
            }
            tx.commit();

        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
        return namespaces;
    }
//
//    @Override
//    public void delete(Namespace ns) {
//        EntityTransaction tx = null;
//        EntityManager em = emf.createEntityManager();
//        try {
//            tx = em.getTransaction();
//            tx.begin();
//            ns = em.find(ns.getClass(), ns.getFqn());
//            for (User u : new HashSet<User>(ns.getUsers())) {
//                // update join
//                ns.removeUser(u);
//                u.removeNamespace(ns);
//                em.merge(u);
//            }
//            em.remove(ns);
//            tx.commit();
//
//        } catch (RuntimeException e) {
//            if (tx != null && tx.isActive()) {
//                tx.rollback();
//            }
//            throw e;
//        } finally {
//            em.close();
//        }
//    }
}
