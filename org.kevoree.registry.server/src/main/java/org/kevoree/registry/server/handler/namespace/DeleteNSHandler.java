package org.kevoree.registry.server.handler.namespace;

import com.eclipsesource.json.JsonObject;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.template.TemplateManager;
import org.kevoree.registry.server.util.RequestHelper;
import org.kevoree.registry.server.util.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * POST /!/ns/delete
 * Created by leiko on 21/11/14.
 */
public class DeleteNSHandler extends AbstractHandler {

    private static final Logger log = LoggerFactory.getLogger(DeleteNSHandler.class.getSimpleName());

    public DeleteNSHandler(TemplateManager manager) {
        super(manager, true);
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        KevUser user = (KevUser) session.getAttribute(SessionHandler.USER);
        // retrieve "namespace" value from form
        String payload = RequestHelper.getStringFrom(exchange);
        try {
            JsonObject data = JsonObject.readFrom(payload);
            String fqn = data.get("fqn").asString();
            if (fqn != null && !fqn.trim().isEmpty()) {
                Namespace ns = NamespaceDAO.getInstance().get(fqn);
                if (ns != null) {
                    // ok
                    if (NamespaceDAO.getInstance().isOwner(ns, user)) {
                        for (KevUser u : ns.getUsers()) {
                            u.removeNamespace(ns);
                        }
                        NamespaceDAO.getInstance().delete(ns);
                        ResponseHelper.ok(exchange);
                    } else {
                        exchange.setResponseCode(StatusCodes.FORBIDDEN);
                        JsonObject response = new JsonObject();
                        response.add("error", "You are not the owner of \""+ns.getFqn()+"\"");
                        ResponseHelper.json(exchange, response);
                    }
                } else {
                    exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                    JsonObject response = new JsonObject();
                    response.add("error", "Unable to find \""+fqn+"\"");
                    ResponseHelper.json(exchange, response);
                }
            } else {
                exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                JsonObject response = new JsonObject();
                response.add("error", "Given fqn is null or empty");
                ResponseHelper.json(exchange, response);
            }
        } catch (Exception e) {
            log.error("500 - Internal Server Error - {}", exchange, e.getMessage());
            log.debug("Caught exception", e);
            exchange.setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
        }
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        exchange.setResponseCode(StatusCodes.NOT_ACCEPTABLE);
        JsonObject response = new JsonObject();
        response.add("error", "Only accept application/json request");
        ResponseHelper.json(exchange, response);
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        handleHTML(exchange);
    }
}
