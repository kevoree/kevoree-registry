package org.kevoree.registry.server.handler;

import io.undertow.io.IoCallback;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.model.User;
import org.kevoree.registry.server.template.TemplateManager;

/**
 * Created by leiko on 28/11/14.
 */
public abstract class AbstractHandler implements HttpHandler {

    private final boolean needAuth;
    protected final Context context;

    public AbstractHandler(Context context, boolean needAuth) {
        this.context = context;
        this.needAuth = needAuth;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));

        User user = (User) session.getAttribute(SessionHandler.USER);
        HeaderValues acceptValues = exchange.getRequestHeaders().get(Headers.ACCEPT);

        if (acceptValues == null || acceptValues.getFirst().startsWith("text/html")) {
            if (needAuth && user == null) {
                new RedirectHandler("/").handleRequest(exchange);
            } else {
                handleHTML(exchange);
            }
        } else if (acceptValues.getFirst().startsWith("application/json")) {
            if (needAuth && user == null) {
                exchange.setResponseCode(StatusCodes.UNAUTHORIZED);
                exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
            } else {
                handleJson(exchange);
            }
        } else {
            if (needAuth && user == null) {
                exchange.setResponseCode(StatusCodes.UNAUTHORIZED);
                exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
            } else {
                handleOther(exchange);
            }
        }
    }

    protected abstract void handleHTML(HttpServerExchange exchange) throws Exception;
    protected abstract void handleJson(HttpServerExchange exchange) throws Exception;
    protected abstract void handleOther(HttpServerExchange exchange) throws Exception;
}
