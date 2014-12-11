package org.kevoree.registry.server.handler.auth;

import com.eclipsesource.json.JsonObject;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.exception.NotAvailableException;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.service.UserService;
import org.kevoree.registry.server.util.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by leiko on 20/11/14.
 */
public class SignInHandler extends AbstractHandler {

    private static final Logger log = LoggerFactory.getLogger(SignInHandler.class.getSimpleName());

    public SignInHandler(Context context) {
        super(context, false);
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        if (session.getAttribute(SessionHandler.USERID) != null) {
            new RedirectHandler("/").handleRequest(exchange);
        } else {
            if (exchange.getRequestMethod().equals(Methods.POST)) {
                // process POST data
                String payload = RequestHelper.getStringFrom(exchange);
                try {
                    JsonObject data = JsonObject.readFrom(payload);
                    String email = data.get("email").asString();
                    String name = data.get("name").asString();
                    String password = data.get("password").asString();

                    UserService userService = UserService.getInstance(context.getEntityManagerFactory());
                    userService.signin(email, name, password);
                    // save it in session
                    session.setAttribute(SessionHandler.USERID, email);
                    // send response
                    exchange.setResponseCode(StatusCodes.CREATED);
                    exchange.getResponseSender().close(IoCallback.END_EXCHANGE);


                } catch (NotAvailableException e) {
                    // error: user id already exists in db
                    exchange.setResponseCode(StatusCodes.CONFLICT);
                    JsonObject response = new JsonObject();
                    response.add("error", e.getMessage());
                    exchange.getResponseSender().send(response.toString());
                    exchange.getResponseSender().close(IoCallback.END_EXCHANGE);

                } catch (Exception e) {
                    log.error("500 - Internal Server Error", exchange);
                    log.debug("Caught exception", e);
                    exchange.setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
                    exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                }

            } else {
                context.getTemplateManager().template(exchange, null, "signin.html");
            }
        }
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        // TODO improve that
        handleHTML(exchange);
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        // TODO improve that
        handleHTML(exchange);
    }
}
