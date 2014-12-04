package org.kevoree.registry.server.dao;

import javax.persistence.*;
import java.util.List;

/**
 * AbstractDAO
 * Created by leiko on 20/11/14.
 */
abstract class AbstractDAO<T> {

    private Class<T> clazz;
    protected final EntityManagerFactory emf;

    protected AbstractDAO(EntityManagerFactory emf, Class<T> clazz) {
        this.emf = emf;
        this.clazz = clazz;
    }

    public T get(String id) {
        T result;
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {
            tx = em.getTransaction();
            tx.begin();
            result = em.find(clazz, id);
            tx.commit();

        } catch (RuntimeException e) {
            if ( tx != null && tx.isActive() ) { tx.rollback(); }
            throw e;
        } finally {
            em.close();
        }

        return result;
    }

    public void update(T t) {
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(t);
            tx.commit();

        } catch (RuntimeException e) {
            if ( tx != null && tx.isActive() ) { tx.rollback(); }
            throw e;
        } finally {
            em.close();
        }
    }

    public void add(T t) {
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(t);
            tx.commit();

        } catch (RuntimeException e) {
            if ( tx != null && tx.isActive() ) { tx.rollback(); }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(T t) {
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {
            tx = em.getTransaction();
            tx.begin();
            t = em.merge(t);
            em.remove(t);
            tx.commit();

        } catch (RuntimeException e) {
            if ( tx != null && tx.isActive() ) { tx.rollback(); }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        List<T> result;
        EntityTransaction tx = null;
        EntityManager em = emf.createEntityManager();
        try {
            tx = em.getTransaction();
            tx.begin();
            TypedQuery<T> query = em.createQuery("SELECT a FROM "+clazz.getSimpleName()+" a", clazz);
            try {
                result = query.getResultList();
            } catch (Exception e) {
                result = null;
            }

            tx.commit();

        } catch (RuntimeException e) {
            if ( tx != null && tx.isActive() ) { tx.rollback(); }
            throw e;
        } finally {
            em.close();
        }

        return result;
    }
}
