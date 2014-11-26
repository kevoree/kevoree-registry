package org.kevoree.registry.server.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.template.TemplateManager;
import org.kevoree.registry.server.util.MD5;

/**
 * Automatically creates a session for each request if none found
 * Created by leiko on 19/11/14.
 */
public class SessionHandler extends AbstractTemplateHandler {

    private HttpHandler next;

    public SessionHandler(TemplateManager manager, HttpHandler next) {
        super(manager);
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        SessionManager manager = exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
        if (manager != null) {
            SessionConfig sessionConfig = exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
            Session session = manager.getSession(exchange, sessionConfig);
            if (session == null) {
                manager.createSession(exchange, sessionConfig);
                tplManager.putLayoutData("connected", false);
            } else {
                if (session.getAttribute("user") == null) {
                    tplManager.putLayoutData("user", null);
                    tplManager.putLayoutData("connected", false);
                } else {
                    KevUser user = (KevUser) session.getAttribute("user");
                    String gravatar = "https://secure.gravatar.com/avatar/" +
                            MD5.md5Hex(user.getGravatarEmail()) +
                            ".jpg?s=50&r=g&d=mm";
                    tplManager.putLayoutData("gravatar", gravatar);
                    tplManager.putLayoutData("user", user);
                    tplManager.putLayoutData("connected", true);
                }
            }
        }
        next.handleRequest(exchange);
    }
}
