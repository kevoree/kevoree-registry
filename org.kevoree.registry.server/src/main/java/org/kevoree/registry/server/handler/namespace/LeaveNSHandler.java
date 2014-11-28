package org.kevoree.registry.server.handler.namespace;

import com.eclipsesource.json.JsonObject;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.dao.KevUserDAO;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.template.TemplateManager;
import org.kevoree.registry.server.util.ResponseHelper;

/**
 * REST /!/ns/leave/:fqn
 * Created by leiko on 21/11/14.
 */
public class LeaveNSHandler extends AbstractHandler {

    public LeaveNSHandler(TemplateManager manager) {
        super(manager, true);
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        String fqn = getQueryFqn(exchange);

        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        KevUser user = (KevUser) session.getAttribute(SessionHandler.USER);

        Namespace ns = NamespaceDAO.getInstance().get(fqn);
        if (ns != null) {
            user.removeNamespace(ns);
            ns.removeUser(user);
            KevUserDAO.getInstance().update(user);
        }

        new RedirectHandler("/").handleRequest(exchange);
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        String fqn = getQueryFqn(exchange);

        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        KevUser user = (KevUser) session.getAttribute(SessionHandler.USER);

        Namespace ns = NamespaceDAO.getInstance().get(fqn);
        if (ns != null) {
            user.removeNamespace(ns);
            ns.removeUser(user);
            KevUserDAO.getInstance().update(user);
            ResponseHelper.ok(exchange);
        } else {
            exchange.setResponseCode(StatusCodes.BAD_REQUEST);
            JsonObject response = new JsonObject();
            response.add("error", "Unknown namespace \""+fqn+"\"");
            ResponseHelper.json(exchange, response);
        }
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        handleHTML(exchange);
    }

    private String getQueryFqn(HttpServerExchange exchange) {
        String fqn = exchange.getRelativePath();
        if (fqn.startsWith("/")) {
            fqn = fqn.substring(1);
        }

        return fqn.trim();
    }
}
