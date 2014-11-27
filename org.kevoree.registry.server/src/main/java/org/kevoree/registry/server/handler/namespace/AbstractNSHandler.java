package org.kevoree.registry.server.handler.namespace;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;

/**
 * Created by leiko on 24/11/14.
 */
abstract class AbstractNSHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        KevUser user = (KevUser) session.getAttribute(SessionHandler.ATTR_USER);
        if (user != null) {
            String fqn = exchange.getRelativePath();
            if (fqn.startsWith("/")) {
                fqn = fqn.substring(1);
            }

            if (fqn.isEmpty()) {
                new RedirectHandler("/").handleRequest(exchange);
            } else {
                Namespace ns = NamespaceDAO.getInstance().get(fqn);
                if (ns != null) {
                    this.handleRequest(user, ns, exchange);
                } else {
                    new RedirectHandler("/").handleRequest(exchange);
                }
            }
        }
    }

    public abstract void handleRequest(KevUser user, Namespace ns, HttpServerExchange exchange)
            throws Exception;
}
