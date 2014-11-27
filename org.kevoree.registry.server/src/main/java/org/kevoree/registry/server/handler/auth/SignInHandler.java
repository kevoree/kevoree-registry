package org.kevoree.registry.server.handler.auth;

import com.eclipsesource.json.JsonObject;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.dao.KevUserDAO;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.template.TemplateManager;
import org.kevoree.registry.server.util.Password;
import org.kevoree.registry.server.util.PasswordHash;
import org.kevoree.registry.server.util.RequestHelper;
import org.kevoree.registry.server.util.ResponseError;

import javax.xml.ws.Response;
import java.util.Iterator;

/**
 *
 * Created by leiko on 20/11/14.
 */
public class SignInHandler extends AbstractTemplateHandler {

    public SignInHandler(TemplateManager manager) {
        super(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));
        if (session.getAttribute(SessionHandler.ATTR_USERID) != null) {
            new RedirectHandler("/").handleRequest(exchange);
        } else {
            if (exchange.getRequestMethod().equals(Methods.POST)) {
                // process POST data
                String payload = RequestHelper.getStringFrom(exchange);
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
                    KevUserDAO.getInstance().add(user);
                    // save it in session
                    session.setAttribute(SessionHandler.ATTR_USERID, email);

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

            } else {
                tplManager.template(exchange, null, "signin.ftl");
            }
        }
    }
}
