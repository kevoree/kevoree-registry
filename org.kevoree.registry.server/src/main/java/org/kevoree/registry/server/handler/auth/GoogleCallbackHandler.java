package org.kevoree.registry.server.handler.auth;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.dao.UserDAO;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.User;
import org.kevoree.registry.server.oauth.google.Auth;
import org.kevoree.registry.server.oauth.google.GoogleOAuth2Manager;

import java.util.Deque;
import java.util.Map;

/**
 * Process the answer received from Google OAuth2 API
 *   - here you get the OAuth code that allows you to request the token
 * Created by leiko on 18/11/14.
 */
public class GoogleCallbackHandler implements HttpHandler {

    private Auth googleAuth;
    private final Context context;

    public GoogleCallbackHandler(Context context, Auth auth) {
        this.context = context;
        this.googleAuth = auth;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        if (session.getAttribute(SessionHandler.USERID) != null) {
            new RedirectHandler("/").handleRequest(exchange);
        } else {
            Map<String, Deque<String>> params = exchange.getQueryParameters();

            if (params.get("error") != null) {
                // user refused the authentication
                new RedirectHandler("/!/auth/signin").handleRequest(exchange);
            } else {
                // user accepted the authentication
                if (session.getAttribute("state") != null) {
                    String sessionState = session.getAttribute("state").toString();
                    if (sessionState.equals(params.get("state").getFirst())) {
                        // session state is consistent: proceed
                        // create HTTPS POST request to retrieve OAuth2 token
                        User user = GoogleOAuth2Manager.getUserInfo(params.get("code").getFirst(), googleAuth);

                        // check if user is already in db
                        if (UserDAO.getInstance(context.getEntityManagerFactory()).get(user.getId()) == null) {
                            // user not in db: add it
                            UserDAO.getInstance(context.getEntityManagerFactory()).add(user);
                        }

                        session.setAttribute(SessionHandler.USERID, user.getId());

                        exchange.setQueryString("");
                        new RedirectHandler("/").handleRequest(exchange);

                    } else {
                        // session state differs: abort
                        // TODO something better than an index redirection
                        exchange.setQueryString("");
                        new RedirectHandler("/").handleRequest(exchange);
                    }
                } else {
                    // unable to find session state: abort
                    // TODO something better than an index redirection
                    exchange.setQueryString("");
                    new RedirectHandler("/").handleRequest(exchange);
                }
            }
        }
    }
}
