package org.kevoree.registry.server.handler;

import io.undertow.io.IoCallback;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.HeaderValues;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.template.TemplateManager;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by leiko on 28/11/14.
 */
public class CSRFHandler extends AbstractTemplateHandler {

    public static final String TOKEN_NAME = "XSRF-TOKEN";

    private static final int DEFAULT_MAXAGE = 30*60; // 30 minutes

    private HttpHandler next;
    private SessionCookieConfig config;
    private SecureRandom random;

    public CSRFHandler(TemplateManager tplManager, HttpHandler next) {
        super(tplManager);
        this.next = next;
        this.config = new SessionCookieConfig();
        this.random = new SecureRandom();

        this.config.setCookieName(TOKEN_NAME);
        this.config.setPath("/");
        this.config.setMaxAge(DEFAULT_MAXAGE);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // set CSRF cookie
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        if (session.getAttribute(TOKEN_NAME) == null) {
            // no CSRF token found for this session
            String token = genToken();
            // set cookie for next response
            addCSRFCookie(exchange, token);
            // set token into session
            session.setAttribute(TOKEN_NAME, token);

        } else {
            // get token from session
            String token = session.getAttribute(TOKEN_NAME).toString();
            // try to retrieve CSRF cookie if any
            Cookie csrfCookie = exchange.getRequestCookies().get(TOKEN_NAME);
            if (csrfCookie != null) {
                // there is a cookie for CSRF in the request
                if (!csrfCookie.getValue().equals(token)) {
                    // cookie value differs from the one in session: replace it
                    addCSRFCookie(exchange, token);
                } else {
                    // CSRF Cookie value matches with CSRF Session value
                    // put token value in template layout data just in case it has been done yet
                    tplManager.putLayoutData(TOKEN_NAME, token);
                }
            } else {
                // set cookie for next response
                addCSRFCookie(exchange, token);
            }
        }

        if (exchange.getRequestMethod().equals(Methods.GET)) {
            next.handleRequest(exchange);
        } else {
            // non-GET method: validate CSRF token before continuing
            HeaderValues csrfToken = exchange.getRequestHeaders().get("X-XSRF-TOKEN");
            if (csrfToken == null) {
                exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                exchange.getResponseSender().send("Missing X-XSRF-TOKEN header");
                exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
            } else {
                if (session.getAttribute(TOKEN_NAME).toString().equals(csrfToken.getFirst())) {
                    next.handleRequest(exchange);
                } else {
                    exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                    exchange.getResponseSender().send("Bad X-XSRF-TOKEN header");
                    exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                }
            }
        }
    }

    private void addCSRFCookie(HttpServerExchange exchange, String token) {
        // set cookie for next response
        config.setSessionId(exchange, token);
        // put token value in template layout data (for form display)
        tplManager.putLayoutData(TOKEN_NAME, token);
    }

    private String genToken() {
        return new BigInteger(130, random).toString(32);
    }
}
