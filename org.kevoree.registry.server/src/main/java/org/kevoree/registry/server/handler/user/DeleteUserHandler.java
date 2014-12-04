package org.kevoree.registry.server.handler.user;

import io.undertow.server.HttpServerExchange;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.AbstractHandler;

/**
 * Created by leiko on 24/11/14.
 */
public class DeleteUserHandler extends AbstractHandler {

    public DeleteUserHandler(Context context) {
        super(context, true);
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        // TODO
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        // TODO
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        // TODO
    }
}
