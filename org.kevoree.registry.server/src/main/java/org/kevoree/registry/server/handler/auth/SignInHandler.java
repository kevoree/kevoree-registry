package org.kevoree.registry.server.handler.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.template.TemplateManager;

/**
 *
 * Created by leiko on 20/11/14.
 */
public class SignInHandler extends AbstractTemplateHandler {

    public SignInHandler(TemplateManager manager) {
        super(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        if (session.getAttribute("user") != null) {
            new RedirectHandler("/").handleRequest(exchange);
        } else {
            tplManager.template(exchange, null, "signin.ftl");
        }
    }
}
