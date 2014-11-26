package org.kevoree.registry.server.dao;

import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;

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
}
