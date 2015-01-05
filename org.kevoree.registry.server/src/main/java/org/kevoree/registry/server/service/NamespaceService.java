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

    /**
     * Add a namespace to the db, using the given user as its owner
     * @param fqn 
     * @param userId
     * @throws NotAvailableException
     * @throws NotValidException
     */
    public void add(String fqn, String userId)
            throws NotAvailableException, NotValidException {
        if (isValid(fqn)) {
            if (isNamespaceAvailable(fqn)) {
                User user = userDAO.get(userId);
                if (user != null) {
                    Namespace namespace = new Namespace();
                    namespace.setFqn(fqn);
                    namespace.setOwner(user);
                    // update user in db
                    user.addNamespace(namespace);
                    userDAO.update(user);
                } else {
                    throw new NotAvailableException("User \""+userId+"\" unknown");
                }
            } else {
                throw new NotAvailableException("Namespace \""+fqn+"\" is not available");
            }
        } else {
            throw new NotValidException("Namespace \""+fqn+"\" is not valid");
        }
    }

    /**
     * Add the given user id to the member of the given namespace fqn
     * @param fqn
     * @param userId
     * @throws NotAvailableException
     */
    public void register(String fqn, String userId) throws NotAvailableException {
        Namespace ns = nsDAO.get(fqn);
        User user = userDAO.get(userId);
        if (ns != null) {
            if (user != null) {
                user.addNamespace(ns);
                userDAO.update(user);
            } else {
                throw new NotAvailableException("User \""+userId+"\" does not exist");
            }
        } else {
            throw new NotAvailableException("Namespace \""+fqn+"\" is not available");
        }
    }

    /**
     *
     * @param fqn
     * @param user
     * @throws OwnerCantLeaveNamespaceException
     * @throws NotAvailableException
     */
    public void leave(String fqn, User user)
            throws OwnerCantLeaveNamespaceException, NotAvailableException {
        Namespace ns = nsDAO.get(fqn);
        if (ns != null) {
            if (ns.getOwner().getId().equals(user.getId())) {
                // prevent owner user from leaving a namespace
                throw new OwnerCantLeaveNamespaceException(user, ns);
            } else {
                user.removeNamespace(ns);
                userDAO.update(user);
            }
        } else {
            throw new NotAvailableException("Namespace \""+fqn+"\" is not available");
        }
    }

    /**
     *
     * @param fqn
     * @param userId
     * @throws OwnerCantLeaveNamespaceException
     * @throws NotAvailableException
     */
    public void leave(String fqn, String userId) throws OwnerCantLeaveNamespaceException, NotAvailableException {
        User user = userDAO.get(userId);
        if (user != null) {
            leave(fqn, user);
        } else {
            throw new NotAvailableException("User \""+userId+"\" does not exist");
        }
    }

    /**
     * Deletes the namespace represented by the given fqn if the user represented by the given userId is the owner
     * of that namespace.
     * @param fqn
     * @param userId
     * @throws NotTheOwnerException
     * @throws NotAvailableException
     */
    public void delete(String fqn, String userId)
            throws NotTheOwnerException, NotAvailableException {
        Namespace ns = nsDAO.get(fqn);
        if (ns != null) {
            User user = userDAO.get(userId);
            if (user != null) {
                if (ns.getOwner().getId().equals(user.getId())) {
                    for (User u : ns.getUsers()) {
                        u.removeNamespace(ns);
                        userDAO.update(u);
                    }
                    nsDAO.delete(ns);

                } else {
                    throw new NotTheOwnerException(user, ns);
                }
            } else {
                throw new NotAvailableException("User \""+userId+"\" does not exist");
            }
        } else {
            throw new NotAvailableException("Namespace \""+fqn+"\" is not available");
        }
    }

    /**
     * Removes every user from the namespace and deletes the namespace from the db
     * @param ns
     */
    public void delete(Namespace ns) {
        ns = nsDAO.get(ns.getFqn());
        for (User u : ns.getUsers()) {
            u.removeNamespace(ns);
            userDAO.update(u);
        }
        nsDAO.delete(ns);
    }

    /**
     *
     * @param fqn
     * @return true if the given fqn is well-formed; false otherwise
     */
    public boolean isValid(String fqn) {
        return fqn.matches("^([a-z_]{2,}(\\.[a-z_]+[0-9]*[a-z_]*)(\\.[a-z_]+[0-9]*[a-z_]*)*)$");
    }

    /**
     * Tells whether or not the given fqn is available
     * If "org.kevoree" is already in the db, then a call to:<br/>
     * <pre>isNamespaceAvailable("org.kevoree.foo")</pre>
     * will return <strong>false</strong>
     * @param fqn
     * @return true if the given fqn is available to register; false otherwise
     */
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

    /**
     * Returns true if the given userId is the ID of the user who owns the namespace that matches the given fqn
     * @param fqn
     * @param userId
     * @throws NotAvailableException if the given fqn or userId is unknown in the db
     * @return
     */
    public boolean isOwner(String fqn, String userId) throws NotAvailableException {
        Namespace ns = nsDAO.get(fqn);
        User user = userDAO.get(userId);
        if (ns != null) {
            if (user != null) {
                return ns.getOwner().getId().equals(user.getId());
            } else {
                throw new NotAvailableException("User \""+userId+"\" does not exist");
            }
        } else {
            throw new NotAvailableException("Namespace \""+fqn+"\" is not available");
        }
    }
}
