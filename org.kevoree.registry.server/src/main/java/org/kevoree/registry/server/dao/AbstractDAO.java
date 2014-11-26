package org.kevoree.registry.server.dao;

import javax.persistence.*;
import java.util.List;

/**
 * AbstractDAO
 * Created by leiko on 20/11/14.
 */
abstract class AbstractDAO<T> {

    private Class<T> clazz;
    protected static final EntityManager manager = Persistence.createEntityManagerFactory("postgres").createEntityManager();

    protected AbstractDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T get(String id) {
        TypedQuery<T> query = manager.createQuery("SELECT a FROM "+clazz.getSimpleName()+" a WHERE a.id = :id", clazz);
        query.setParameter("id", id);
        T result;
        try {
            result = query.getSingleResult();
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    public void update(T t) {
        manager.getTransaction().begin();
        manager.merge(t);
        manager.getTransaction().commit();
    }

    public void add(T t) {
        manager.getTransaction().begin();
        manager.persist(t);
        manager.getTransaction().commit();
    }

    public void delete(T t) {
        manager.getTransaction().begin();
        manager.remove(t);
        manager.getTransaction().commit();
    }

    public List<T> findAll() {
        TypedQuery<T> query = manager.createQuery("SELECT a FROM "+clazz.getSimpleName()+" a", clazz);
        List<T> result;
        try {
            result = query.getResultList();
        } catch (Exception e) {
            result = null;
        }
        return result;
    }
}
