package org.kevoree.registry.server.handler.auth;

import com.eclipsesource.json.JsonObject;
import freemarker.template.SimpleHash;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.dao.KevUserDAO;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.handler.CSRFHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.template.TemplateManager;
import org.kevoree.registry.server.util.Password;
import org.kevoree.registry.server.util.PasswordHash;
import org.kevoree.registry.server.util.RequestHelper;
import org.kevoree.registry.server.util.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Response;

/**
 *
 * Created by leiko on 20/11/14.
 */
public class LogInHandler extends AbstractTemplateHandler {

    private static final Logger log = LoggerFactory.getLogger(LogInHandler.class.getSimpleName());

    public LogInHandler(TemplateManager manager) {
        super(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
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
                    String csrfToken = data.get(CSRFHandler.TOKEN_NAME).asString();
                    if (csrfToken == session.getAttribute(CSRFHandler.TOKEN_NAME)) {
                        // CSRF Token valid
                        String email = data.get("email").asString();
                        String password = data.get("password").asString();

                        KevUser user = KevUserDAO.getInstance().get(email);
                        if (user == null) {
                            // error: user id unknown
                            exchange.setResponseCode(StatusCodes.NOT_FOUND);
                            JsonObject response = new JsonObject();
                            response.add("message", "Email address \"" + email + "\" unknown");
                            ResponseHelper.json(exchange, response);
                        } else {
                            // email address is available in db: check for password match
                            if (user.getSalt() == null) {
                                // seems like this email has been saved using OAuth so there is no password
                                exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                                JsonObject response = new JsonObject();
                                response.add("message", "It seems like this email address has been registered using OpenID, retry connection using OpenID SignIn service and edit your password in your profile.");
                                ResponseHelper.json(exchange, response);

                            } else {
                                Password hashedPassword = new Password(PasswordHash.PBKDF2_ITERATIONS, user.getPassword(), user.getSalt());
                                if (PasswordHash.validatePassword(password, hashedPassword)) {
                                    // valid authentication
                                    // save user in session
                                    user.setSessionId(session.getId());
                                    KevUserDAO.getInstance().update(user);
                                    session.setAttribute(SessionHandler.USERID, email);
                                    // send response
                                    exchange.setResponseCode(StatusCodes.OK);
                                    exchange.getResponseSender().close(IoCallback.END_EXCHANGE);

                                } else {
                                    // wrong password
                                    exchange.setResponseCode(StatusCodes.FORBIDDEN);
                                    JsonObject response = new JsonObject();
                                    response.add("message", "Please enter a correct email and password");
                                    ResponseHelper.json(exchange, response);
                                }
                            }
                        }
                    } else {
                        // CSRF Token invalid
                        exchange.setResponseCode(StatusCodes.FORBIDDEN);
                        JsonObject response = new JsonObject();
                        response.add("message", "CSRF Token invalid");
                        ResponseHelper.json(exchange, response);
                    }
                } catch (Exception e) {
                    log.error("500 - Internal Server Error", exchange);
                    log.debug("Caught exception", e);
                    exchange.setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
                    exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                }

            } else {
                tplManager.template(exchange, null, "login.ftl");
            }
        }
    }
}
