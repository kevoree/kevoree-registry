package org.kevoree.registry.server.handler.user;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.dao.KevUserDAO;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.template.TemplateManager;
import org.kevoree.registry.server.util.Password;
import org.kevoree.registry.server.util.PasswordHash;
import org.kevoree.registry.server.util.RequestHelper;
import org.kevoree.registry.server.util.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by leiko on 24/11/14.
 */
public class EditUserHandler extends AbstractHandler {

    private static final Logger log = LoggerFactory.getLogger(EditUserHandler.class.getSimpleName());

    public EditUserHandler(TemplateManager manager) {
        super(manager, true);
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        KevUser user = (KevUser) session.getAttribute(SessionHandler.USER);
        // retrieve values from request payload
        String payload = RequestHelper.getStringFrom(exchange);
        try {
            JsonObject data = JsonObject.readFrom(payload);
            if (data.get("new_pass") != null) {
                editPassword(exchange, user, data);
            } else if (data.get("gravatar_email") != null) {
                JsonValue gravatarVal = data.get("gravatar_email");
                user.setGravatarEmail(gravatarVal.asString());
                KevUserDAO.getInstance().update(user);
                ResponseHelper.ok(exchange);
            } else {
                exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                JsonObject response = new JsonObject();
                response.add("error", "Malformed request payload");
                ResponseHelper.json(exchange, response);
            }
        } catch (Exception e) {
            log.error("500 - Internal Server Error - {}", exchange, e.getMessage());
            log.debug("Caught exception", e);
            exchange.setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
        }
    }

    private void editPassword(HttpServerExchange exchange, KevUser user, JsonObject data) throws Exception {
        JsonValue oldPassVal = data.get("old_pass");
        JsonValue newPassVal = data.get("new_pass");

        if (user.getSalt() == null) {
            // account created from OAuth2 (no old password yet)
            String newPass = newPassVal.asString();
            if (!newPass.trim().isEmpty() && newPass.trim().length() >= 8) {
                Password pass = PasswordHash.createHash(newPass);
                user.setPassword(pass.getHash());
                user.setSalt(pass.getSalt());
                KevUserDAO.getInstance().update(user);
                ResponseHelper.ok(exchange);
            } else {
                exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                JsonObject response = new JsonObject();
                response.add("error", "Password must be at least 8 characters long");
                ResponseHelper.json(exchange, response);
            }
        } else {
            if (oldPassVal != null) {
                // check old password before updating
                String oldPass = oldPassVal.asString();
                String newPass = newPassVal.asString();
                if (!oldPass.trim().isEmpty() && !newPass.trim().isEmpty() && newPass.trim().length() >= 8) {
                    Password hashedPassword = new Password(PasswordHash.PBKDF2_ITERATIONS, user.getPassword(), user.getSalt());
                    if (PasswordHash.validatePassword(oldPass, hashedPassword)) {
                        // valid authentication
                        Password pass = PasswordHash.createHash(newPass);
                        user.setPassword(pass.getHash());
                        user.setSalt(pass.getSalt());
                        KevUserDAO.getInstance().update(user);
                        ResponseHelper.ok(exchange);

                    } else {
                        // wrong password
                        exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                        JsonObject response = new JsonObject();
                        response.add("error", "Wrong old password");
                        ResponseHelper.json(exchange, response);
                    }
                } else {
                    exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                    JsonObject response = new JsonObject();
                    response.add("error", "Password must be at least 8 characters long");
                    ResponseHelper.json(exchange, response);
                }
            } else {
                exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                JsonObject response = new JsonObject();
                response.add("error", "Malformed request payload");
                ResponseHelper.json(exchange, response);
            }
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
