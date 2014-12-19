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
import org.kevoree.registry.server.dao.UserDAO;
import org.kevoree.registry.server.exception.NotAvailableException;
import org.kevoree.registry.server.exception.NotValidException;
import org.kevoree.registry.server.exception.PasswordException;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.User;
import org.kevoree.registry.server.service.UserService;
import org.kevoree.registry.server.util.Password;
import org.kevoree.registry.server.util.PasswordHash;
import org.kevoree.registry.server.util.RequestHelper;
import org.kevoree.registry.server.util.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by leiko on 20/11/14.
 */
public class LogInHandler extends AbstractHandler {

    private static final Logger log = LoggerFactory.getLogger(LogInHandler.class.getSimpleName());

    public LogInHandler(Context context) {
        super(context, false);
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        if (session.getAttribute(SessionHandler.USERID) != null) {
            exchange.setResponseCode(StatusCodes.BAD_REQUEST);
            JsonObject response = new JsonObject();
            response.add("error", "Already logged in");
            ResponseHelper.json(exchange, response);

        } else {
            if (exchange.getRequestMethod().equals(Methods.POST)) {
                // process POST data
                String payload = RequestHelper.getStringFrom(exchange);
                try {
                    JsonObject data = JsonObject.readFrom(payload);
                    String email = data.get("email").asString();
                    String password = data.get("password").asString();

                    UserService userService = UserService.getInstance(context.getEntityManagerFactory());
                    userService.login(email, password);
                    // save in session
                    session.setAttribute(SessionHandler.USERID, email);
                    ResponseHelper.ok(exchange);

                } catch (NotAvailableException e) {
                    // user id unknown
                    exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                    JsonObject response = new JsonObject();
                    response.add("error", e.getMessage());
                    ResponseHelper.json(exchange, response);

                } catch (NotValidException e) {
                    // Account issued from OpenID sign-in
                    exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                    JsonObject response = new JsonObject();
                    response.add("error", e.getMessage());
                    ResponseHelper.json(exchange, response);

                } catch (PasswordException e) {
                    // wrong password
                    exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                    JsonObject response = new JsonObject();
                    response.add("error", e.getMessage());
                    ResponseHelper.json(exchange, response);

                } catch (Exception e) {
                    log.error("500 - Internal Server Error", exchange);
                    log.debug("Caught exception", e);
                    exchange.setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
                    exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                }

            } else {
                exchange.setResponseCode(StatusCodes.METHOD_NOT_ALLOWED);
                JsonObject response = new JsonObject();
                response.add("error", "Only POST request accepted when expecting JSON response");
                ResponseHelper.json(exchange, response);
            }
        }
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        if (session.getAttribute(SessionHandler.USERID) != null) {
            new RedirectHandler("/").handleRequest(exchange);
        } else {
            context.getTemplateManager().template(exchange, null, "login.html");
        }
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        this.handleHTML(exchange);
    }
}
