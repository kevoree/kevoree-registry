package org.kevoree.registry.server.router;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.ResponseCodeHandler;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.handler.user.EditHandler;
import org.kevoree.registry.server.handler.user.ProfileHandler;
import org.kevoree.registry.server.template.TemplateManager;

import static org.kevoree.registry.server.util.RequestHelper.post;

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
            .addPrefixPath("/delete", ResponseCodeHandler.HANDLE_404) // TODO
            .addPrefixPath("/edit", post(new EditHandler(tplManager)))
            .addPrefixPath("/", new ProfileHandler(tplManager))
            .handleRequest(exchange);
    }
}
