package org.kevoree.registry.server.handler.namespace;

import com.eclipsesource.json.JsonObject;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.exception.NotAvailableException;
import org.kevoree.registry.server.exception.NotTheOwnerException;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;
import org.kevoree.registry.server.service.NamespaceService;
import org.kevoree.registry.server.service.UserService;
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

    public DeleteNSHandler(Context context) {
        super(context, true);
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        User user = (User) session.getAttribute(SessionHandler.USER);
        // retrieve "namespace" value from form
        String payload = RequestHelper.getStringFrom(exchange);
        try {
            JsonObject data = JsonObject.readFrom(payload);
            String fqn = data.get("fqn").asString();
            if (fqn != null && !fqn.trim().isEmpty()) {
                NamespaceService nsService = NamespaceService.getInstance(context.getEntityManagerFactory());
                nsService.delete(fqn, user);
                ResponseHelper.ok(exchange);

            } else {
                exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                JsonObject response = new JsonObject();
                response.add("error", "Given fqn is null or empty");
                ResponseHelper.json(exchange, response);
            }
        } catch (NotTheOwnerException e) {
            exchange.setResponseCode(StatusCodes.FORBIDDEN);
            JsonObject response = new JsonObject();
            response.add("error", e.getMessage());
            ResponseHelper.json(exchange, response);

        } catch (NotAvailableException e) {
            exchange.setResponseCode(StatusCodes.BAD_REQUEST);
            JsonObject response = new JsonObject();
            response.add("error", e.getMessage());
            ResponseHelper.json(exchange, response);

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
