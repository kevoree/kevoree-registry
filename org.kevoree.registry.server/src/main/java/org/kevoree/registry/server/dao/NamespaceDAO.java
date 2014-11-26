package org.kevoree.registry.server.dao;

import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;

import javax.persistence.TypedQuery;
import java.util.List;

/**
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

    public boolean isOwner(Namespace ns, KevUser user) {
        TypedQuery<String> query = manager.createQuery(
                "SELECT u.id FROM " +
                        KevUser.class.getSimpleName() +
                        " u, " +
                        Namespace.class.getSimpleName() +
                        " n WHERE u.id = :id AND n.fqn = :fqn",
                String.class);
        query.setParameter("id", user.getId());
        query.setParameter("fqn", ns.getFqn());
        boolean result;
        try {
            List<String> results = query.getResultList();
            result = (results.size() > 0);
        } catch (Exception e) {
            result = false;
        }

        return result;
    }
}
