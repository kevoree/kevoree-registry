package org.kevoree.registry.server;

import org.kevoree.factory.KevoreeFactory;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.registry.server.template.TemplateManager;

import javax.persistence.EntityManagerFactory;

/**
 *
 * Created by leiko on 04/12/14.
 */
public class Context {

    private final EntityManagerFactory emf;
    private final TemplateManager tplManager;
    private final KevoreeTransactionManager transManager;
    private final KevoreeFactory factory;

    public Context(EntityManagerFactory emf, TemplateManager tplManager, KevoreeTransactionManager transManager, KevoreeFactory factory) {
        this.emf = emf;
        this.tplManager = tplManager;
        this.transManager = transManager;
        this.factory = factory;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return this.emf;
    }

    public TemplateManager getTemplateManager() {
        return tplManager;
    }

    public KevoreeTransactionManager getKevoreeTransactionManager() {
        return transManager;
    }

    public KevoreeFactory getKevoreeFactory() {
        return factory;
    }
}
