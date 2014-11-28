package org.kevoree.registry.server.dao;

import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;

import javax.persistence.TypedQuery;

/**
 * Created by leiko on 20/11/14.
 */
public class KevUserDAO extends AbstractDAO<KevUser> {

    public static KevUserDAO INSTANCE;

    private KevUserDAO() {
        super(KevUser.class);
    }

    public static KevUserDAO getInstance() {
        if (KevUserDAO.INSTANCE == null) {
            KevUserDAO.INSTANCE = new KevUserDAO();
        }
        return KevUserDAO.INSTANCE;
    }

    public void deleteNamespace(KevUser u, Namespace n) {
        if (n.getOwner().getId().equals(u.getId())) {
            u.removeNamespace(n);
            n.removeUser(u);

            this.update(u);
            NamespaceDAO.getInstance().update(n);

            if (n.getUsers().isEmpty()) {
                NamespaceDAO.getInstance().delete(n);
            }
        }
    }


    public KevUser findUserBySession(String sessId) {
        TypedQuery<KevUser> query = manager.createQuery("SELECT a FROM " +
                KevUser.class.getSimpleName() +
                " a WHERE a.sessionId = :sessId", KevUser.class);
        query.setParameter("sessId", sessId);
        KevUser result;
        try {
            result = query.getSingleResult();
        } catch (Exception e) {
            result = null;
        }

        return result;
    }
}
