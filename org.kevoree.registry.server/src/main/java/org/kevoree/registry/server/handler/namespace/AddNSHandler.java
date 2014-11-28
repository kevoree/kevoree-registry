package org.kevoree.registry.server.handler.namespace;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.kevoree.registry.server.dao.KevUserDAO;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;

/**
 * API /!/ns/add
 * Created by leiko on 24/11/14.
 */
public class AddNSHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        KevUser user = (KevUser) session.getAttribute(SessionHandler.USER);
        if (user != null) {
            // retrieve "namespace" value from form
            FormEncodedDataDefinition form = new FormEncodedDataDefinition();
            FormDataParser parser = form.create(exchange);
            FormData data = parser.parseBlocking();
            String fqn = data.getFirst("namespace").getValue();

            if (fqn != null && !fqn.isEmpty()) {
                // try to find namespace in db
                Namespace namespace = NamespaceDAO.getInstance().get(fqn);
                if (namespace == null) {
                    // namespace is available: save it for that user
                    namespace = new Namespace();
                    namespace.setFqn(fqn);
                    namespace.setOwner(user);
                    namespace.addUser(user);
                    user.addNamespace(namespace);
                    KevUserDAO.getInstance().update(user);

                    HeaderValues referer = exchange.getRequestHeaders().get(Headers.REFERER);
                    if (referer == null) {
                        new RedirectHandler("/").handleRequest(exchange);
                    } else {
                        new RedirectHandler(referer.getFirst()).handleRequest(exchange);
                    }

                } else {
                    // namespace is already owned by someone else
                    // TODO show error namespace already owned by someone else
                }
            } else {
                HeaderValues referer = exchange.getRequestHeaders().get(Headers.REFERER);
                if (referer == null) {
                    new RedirectHandler("/").handleRequest(exchange);
                } else {
                    new RedirectHandler(referer.getFirst()).handleRequest(exchange);
                }
            }
        } else {
            new RedirectHandler("/").handleRequest(exchange);
        }
    }
}
