package org.kevoree.registry.server.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import org.kevoree.registry.server.dao.KevUserDAO;
import org.kevoree.registry.server.model.KevUser;

/**
 * Automatically creates a session for each request if none found
 * Created by leiko on 19/11/14.
 */
public class SessionHandler implements HttpHandler {

    public static final String USERID = "userId";
    public static final String USER = "user";

    private HttpHandler next;

    public SessionHandler(HttpHandler next) {
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
            } else {
                if (session.getAttribute(USERID) != null) {
                    // user found in session: ok
                    String userId = (String) session.getAttribute(USERID);
                    KevUser user = KevUserDAO.getInstance().get(userId);
                    // update user in session
                    session.setAttribute(USER, user);
                }
            }
        }
        next.handleRequest(exchange);
    }
}
