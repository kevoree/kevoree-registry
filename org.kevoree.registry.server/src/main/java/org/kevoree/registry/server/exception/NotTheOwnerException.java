package org.kevoree.registry.server.exception;

import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

/**
 * Created by leiko on 11/12/14.
 */
public class NotTheOwnerException extends Exception {

    public NotTheOwnerException(User user, Namespace ns) {
        super("User \""+user.getId()+"\" is not the owner of the namespace \""+ns.getFqn()+"\", therefore he cannot delete it");
    }
}
