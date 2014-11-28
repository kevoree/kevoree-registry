package org.kevoree.registry.server.router;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.handler.user.DeleteUserHandler;
import org.kevoree.registry.server.handler.user.EditUserHandler;
import org.kevoree.registry.server.handler.user.ProfileHandler;
import org.kevoree.registry.server.template.TemplateManager;

import static org.kevoree.registry.server.util.RequestHelper.post;
import static org.kevoree.registry.server.util.RequestHelper.get;

/**
 * API /user router
 * Created by leiko on 24/11/14.
 */
public class UserRouter extends AbstractTemplateHandler {

    public UserRouter(TemplateManager manager) {
        super(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        new PathHandler()
                .addPrefixPath("/delete", post(new DeleteUserHandler(tplManager)))
                .addPrefixPath("/edit", post(new EditUserHandler(tplManager)))
                .addPrefixPath("/", get(new ProfileHandler(tplManager)))
                .handleRequest(exchange);
    }
}
