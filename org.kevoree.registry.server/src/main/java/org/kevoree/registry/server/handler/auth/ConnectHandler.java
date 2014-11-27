package org.kevoree.registry.server.handler.auth;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.oauth.google.Auth;
import org.kevoree.registry.server.oauth.google.GoogleOAuth2Manager;

import java.util.UUID;

/**
 * Redirects the user to Google OAuth2 API
 * Created by leiko on 17/11/14.
 */
public class ConnectHandler implements HttpHandler {
    private Auth googleAuth;

    public ConnectHandler(Auth auth) {
        this.googleAuth = auth;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        if (session.getAttribute(SessionHandler.ATTR_USERID) != null) {
            new RedirectHandler("/").handleRequest(exchange);
        } else {
            googleAuth.state = UUID.randomUUID().toString();
            googleAuth.redirectURI = "http://" + exchange.getHostName() + "/!/auth/gcallback";
            // save state in session
            session.setAttribute("state", googleAuth.state);

            HttpHandler redirect = new RedirectHandler(GoogleOAuth2Manager.authEndPoint(googleAuth));
            redirect.handleRequest(exchange);
        }
    }
}
