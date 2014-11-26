package org.kevoree.registry.server.handler.user;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import org.kevoree.registry.server.dao.KevUserDAO;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.template.TemplateManager;

/**
 * Created by leiko on 24/11/14.
 */
public class EditHandler extends AbstractTemplateHandler {

    public EditHandler(TemplateManager manager) {
        super(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        if (session.getAttribute("user") != null) {
            KevUser user = (KevUser) session.getAttribute("user");

            // retrieve "namespace" value from form
            FormEncodedDataDefinition form = new FormEncodedDataDefinition();
            FormDataParser parser = form.create(exchange);
            FormData data = parser.parseBlocking();
            String gravatarEmail = data.getFirst("gravatar_email").getValue();

            user.setGravatarEmail(gravatarEmail);
            KevUserDAO.getInstance().update(user);
            new RedirectHandler("/!/user").handleRequest(exchange);


        } else {
            new RedirectHandler("/").handleRequest(exchange);
        }
    }
}
