package org.kevoree.registry.server.handler.user;

import freemarker.template.SimpleHash;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.template.TemplateManager;

/**
 * Created by leiko on 20/11/14.
 */
public class ProfileHandler extends AbstractTemplateHandler {

    public ProfileHandler(TemplateManager manager) {
        super(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        if (session.getAttribute("user") != null) {
            KevUser user = (KevUser) session.getAttribute("user");

            SimpleHash data = new SimpleHash();
            // TODO add CSRF token for the form
            data.put("user", user);
            data.put("namespaces", user.getNamespaces());
            tplManager.template(exchange, data, "profile.ftl");

        } else {
            new RedirectHandler("/").handleRequest(exchange);
        }
    }
}
