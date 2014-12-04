package org.kevoree.registry.server.handler.namespace;

import com.eclipsesource.json.JsonArray;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.Headers;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.User;
import org.kevoree.registry.server.model.Namespace;

/**
 * Created by leiko on 28/11/14.
 */
public class GetNSHandler extends AbstractHandler {

    public GetNSHandler(Context context) {
        super(context, true);
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        context.getTemplateManager().template(exchange, "namespaces.ftl");
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        User user = (User) session.getAttribute(SessionHandler.USER);
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        JsonArray namespaces = new JsonArray();
        for (Namespace ns : user.getNamespaces()) {
            namespaces.add(ns.toJson());
        }
        exchange.getResponseSender().send(namespaces.toString());
        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        handleHTML(exchange);
    }
}
