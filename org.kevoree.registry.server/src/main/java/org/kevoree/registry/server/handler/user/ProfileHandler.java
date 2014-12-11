package org.kevoree.registry.server.handler.user;

import com.eclipsesource.json.JsonObject;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.User;
import org.kevoree.registry.server.util.ResponseHelper;

/**
 * API /!/user
 * Created by leiko on 20/11/14.
 */
public class ProfileHandler extends AbstractHandler {

    public ProfileHandler(Context context) {
        super(context, false);
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        User user = (User) session.getAttribute(SessionHandler.USER);
        if (user != null) {
            context.getTemplateManager().template(exchange, "profile.html");
        } else {
            new RedirectHandler("/").handleRequest(exchange);
        }
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        User user = (User) session.getAttribute(SessionHandler.USER);
        if (user != null) {
            ResponseHelper.json(exchange, user.toJson());
        } else {
            exchange.setResponseCode(StatusCodes.UNAUTHORIZED);
            JsonObject response = new JsonObject();
            response.add("error", "Not connected");
            ResponseHelper.json(exchange, response);
        }
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        handleHTML(exchange);
    }
}
