package org.kevoree.registry.server.handler.namespace;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.util.Headers;
import org.kevoree.registry.server.dao.KevUserDAO;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;

/**
 * REST namespace/leave/:fqn
 * Created by leiko on 21/11/14.
 */
public class LeaveNSHandler extends AbstractNSHandler {


    @Override
    public void handleRequest(KevUser user, Namespace ns, HttpServerExchange exchange) throws Exception {
        user.removeNamespace(ns);
        ns.removeUser(user);
        KevUserDAO.getInstance().update(user);
        new RedirectHandler(exchange.getRequestHeaders().get(Headers.REFERER).getFirst()).handleRequest(exchange);
    }
}
