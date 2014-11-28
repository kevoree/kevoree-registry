package org.kevoree.registry.server.handler.user;

import com.eclipsesource.json.JsonObject;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.template.TemplateManager;
import org.kevoree.registry.server.util.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by leiko on 24/11/14.
 */
public class EditUserHandler extends AbstractTemplateHandler {

    private static final Logger log = LoggerFactory.getLogger(EditUserHandler.class.getSimpleName());

    public EditUserHandler(TemplateManager manager) {
        super(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        KevUser user = (KevUser) session.getAttribute(SessionHandler.USER);
        if (user != null) {
            // retrieve data
            String payload = RequestHelper.getStringFrom(exchange);
            try {
                JsonObject data = JsonObject.readFrom(payload);
            } catch (Exception e) {
                log.error("500 - Internal Server Error", exchange);
                log.debug("Caught exception", e);
                exchange.setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
                exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
            }

            if (user.getSalt() == null) {
                // this user has logged in using OAuth and never updated
            }

        } else {
            new RedirectHandler("/").handleRequest(exchange);
        }
    }
}
