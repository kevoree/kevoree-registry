package org.kevoree.registry.server.handler.auth;

import com.eclipsesource.json.JsonObject;
import freemarker.template.SimpleHash;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by leiko on 20/11/14.
 */
public class SignInHandler extends AbstractTemplateHandler {

    private static final Logger log = LoggerFactory.getLogger(SignInHandler.class.getSimpleName());

    public SignInHandler(TemplateManager manager) {
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
                    String email = data.get("email").asString();
                    String name = data.get("name").asString();
                    String password = data.get("password").asString();

                    KevUser user = KevUserDAO.getInstance().get(email);
                    if (user == null) {
                        // this user id is available
                        user = new KevUser();
                        user.setId(email);
                        user.setGravatarEmail(email);
                        user.setName(name);

                        Password hashedPassword = PasswordHash.createHash(password);
                        user.setSalt(hashedPassword.getSalt());
                        user.setPassword(hashedPassword.getHash());
                        // save it in db
                        user.setSessionId(session.getId());
                        KevUserDAO.getInstance().add(user);
                        // save it in session
                        session.setAttribute(SessionHandler.USERID, email);

                        exchange.setResponseCode(StatusCodes.CREATED);
                        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);

                    } else {
                        // error: user id already exists in db
                        exchange.setResponseCode(StatusCodes.CONFLICT);
                        JsonObject response = new JsonObject();
                        response.add("message", "This email address is already associated with an account");
                        exchange.getResponseSender().send(response.toString());
                        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                    }
                } catch (Exception e) {
                    log.error("500 - Internal Server Error", exchange);
                    log.debug("Caught exception", e);
                    exchange.setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
                    exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                }

            } else {
                tplManager.template(exchange, null, "signin.ftl");
            }
        }
    }
}
