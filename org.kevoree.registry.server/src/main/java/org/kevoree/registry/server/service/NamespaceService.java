package org.kevoree.registry.server.service;

import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.dao.UserDAO;
import org.kevoree.registry.server.exception.NotAvailableException;
import org.kevoree.registry.server.exception.NotTheOwnerException;
import org.kevoree.registry.server.exception.NotValidException;
import org.kevoree.registry.server.exception.OwnerCantLeaveNamespaceException;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 *
 * Created by leiko on 10/12/14.
 */
public class NamespaceService {

    private static NamespaceService INSTANCE;

    private final NamespaceDAO nsDAO;
    private final UserDAO userDAO;

    private NamespaceService(EntityManagerFactory emf) {
        nsDAO = NamespaceDAO.getInstance(emf);
        userDAO = UserDAO.getInstance(emf);
    }

    public static NamespaceService getInstance(EntityManagerFactory emf) {
        if (NamespaceService.INSTANCE == null) {
            NamespaceService.INSTANCE = new NamespaceService(emf);
        }

        return NamespaceService.INSTANCE;
    }

    public void add(String fqn, User user)
            throws NotAvailableException, NotValidException {
        if (isValid(fqn)) {
            if (isNamespaceAvailable(fqn)) {
                Namespace namespace = new Namespace();
                namespace.setFqn(fqn);
                namespace.setOwner(user);
                // update user in db
                user.addNamespace(namespace);
                userDAO.update(user);
            } else {
                throw new NotAvailableException("Namespace \""+fqn+"\" is not available");
            }
        } else {
            throw new NotValidException("Namespace \""+fqn+"\" is not valid");
        }
    }

    public void join(String fqn, User user)
            throws NotAvailableException, NotValidException {
        if (isValid(fqn)) {
            Namespace namespace = nsDAO.get(fqn);
            if (namespace != null) {
                // update user in db
                user.addNamespace(namespace);
                userDAO.update(user);
            } else {
                throw new NotAvailableException("Namespace \""+fqn+"\" is not available");
            }
        } else {
            throw new NotValidException("Namespace \""+fqn+"\" is not valid");
        }
    }

    public void leave(String fqn, User user)
            throws OwnerCantLeaveNamespaceException, NotAvailableException {
        Namespace ns = nsDAO.get(fqn);
        if (ns != null) {
//            user = userDAO.get(user.getId());
            if (ns.getOwner().getId().equals(user.getId())) {
                // prevent owner user from leaving a namespace
                throw new OwnerCantLeaveNamespaceException(user, ns);
            } else {
                System.out.println(user.toString());
                System.out.println("REMOVING "+ns.getFqn()+" FROM "+user.getId());
                user.removeNamespace(ns);
                System.out.println(user.toString());
                userDAO.update(user);
            }
        } else {
            throw new NotAvailableException("Namespace \""+fqn+"\" is not available");
        }
    }

    public void delete(String fqn, User user)
            throws NotTheOwnerException, NotAvailableException {
        Namespace ns = nsDAO.get(fqn);
        if (ns != null) {
            if (ns.getOwner().getId().equals(user.getId())) {
                nsDAO.delete(ns);

            } else {
                throw new NotTheOwnerException(user, ns);
            }
        } else {
            throw new NotAvailableException("Namespace \""+fqn+"\" is not available");
        }
    }

    public void delete(Namespace ns) {
        ns = nsDAO.get(ns.getFqn());
        for (User u : ns.getUsers()) {
            u.removeNamespace(ns);
        }
        nsDAO.delete(ns);
    }

    public boolean isValid(String fqn) {
        return fqn.matches("^([a-z_]{2,}(\\.[a-z_]+[0-9]*[a-z_]*)(\\.[a-z_]+[0-9]*[a-z_]*)*)$");
    }

    public boolean isNamespaceAvailable(String fqn) {
        List<Namespace> namespaces = nsDAO.getAll();
        for (Namespace ns : namespaces) {
            if (ns.getFqn().startsWith(fqn) || fqn.startsWith(ns.getFqn())) {
                // you cannot add a sub-namespace of a previously registered namespace
                return false;
            }
        }
        return true;
    }
}
