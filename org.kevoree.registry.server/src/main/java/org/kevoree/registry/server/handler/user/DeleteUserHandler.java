package org.kevoree.registry.server.handler.user;

import io.undertow.server.HttpServerExchange;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.template.TemplateManager;

/**
 * Created by leiko on 24/11/14.
 */
public class DeleteUserHandler extends AbstractTemplateHandler {

    public DeleteUserHandler(TemplateManager manager) {
        super(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // TODO
    }
}
