package org.kevoree.registry.server.handler.namespace;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.template.TemplateManager;

/**
 * REST /!/ns/delete/:fqn
 * Created by leiko on 21/11/14.
 */
public class DeleteNSHandler extends AbstractHandler {

    public DeleteNSHandler(TemplateManager manager) {
        super(manager, true);
    }

    @Override
    public void handleRequest(KevUser user, Namespace ns, HttpServerExchange exchange) throws Exception {
        if (NamespaceDAO.getInstance().isOwner(ns, user)) {
            for (KevUser u : ns.getUsers()) {
                u.removeNamespace(ns);
            }
            NamespaceDAO.getInstance().delete(ns);

            HeaderValues referer = exchange.getRequestHeaders().get(Headers.REFERER);
            if (referer == null) {
                new RedirectHandler("/").handleRequest(exchange);
            } else {
                new RedirectHandler(referer.getFirst()).handleRequest(exchange);
            }
        } else {
            new RedirectHandler("/").handleRequest(exchange);
        }
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {

    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {

    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {

    }
}
