package org.kevoree.registry.server.router;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.user.DeleteUserHandler;
import org.kevoree.registry.server.handler.user.EditUserHandler;
import org.kevoree.registry.server.handler.user.ProfileHandler;

import static org.kevoree.registry.server.util.RequestHelper.get;
import static org.kevoree.registry.server.util.RequestHelper.post;

/**
 * API /user router
 * Created by leiko on 24/11/14.
 */
public class UserRouter extends AbstractRouter {

    public UserRouter(Context context) {
        super(context);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        new PathHandler()
                .addPrefixPath("/delete", post(new DeleteUserHandler(context)))
                .addPrefixPath("/edit", post(new EditUserHandler(context)))
                .addPrefixPath("/", get(new ProfileHandler(context)))
                .handleRequest(exchange);
    }
}
