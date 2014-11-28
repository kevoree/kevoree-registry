package org.kevoree.registry.server.handler.user;

import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.template.TemplateManager;

/**
 * API /!/user
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
        KevUser user = (KevUser) session.getAttribute(SessionHandler.USER);

        HeaderValues acceptValues = exchange.getRequestHeaders().get(Headers.ACCEPT);
        if (acceptValues != null) {
            if (acceptValues.getFirst().equals("application/json")) {
                if (user != null) {
                    exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send(user.toJson().toString());
                    exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                } else {
                    exchange.setResponseCode(StatusCodes.FORBIDDEN);
                    exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                }
            } else {
                if (user != null) {
                    tplManager.template(exchange, "profile.ftl");

                } else {
                    new RedirectHandler("/").handleRequest(exchange);
                }
            }
        } else {
            if (user != null) {
                tplManager.template(exchange, "profile.ftl");

            } else {
                new RedirectHandler("/").handleRequest(exchange);
            }
        }
    }
}
