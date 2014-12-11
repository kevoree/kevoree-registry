package org.kevoree.registry.server.exception;

import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

/**
 * Created by leiko on 11/12/14.
 */
public class OwnerCantLeaveNamespaceException extends Exception {
    public OwnerCantLeaveNamespaceException(User user, Namespace ns) {
        super("User \""+user.getId()+"\" is the owner of namespace \""+ns.getFqn()+"\", therefore he cannot leave it.");
    }
}
