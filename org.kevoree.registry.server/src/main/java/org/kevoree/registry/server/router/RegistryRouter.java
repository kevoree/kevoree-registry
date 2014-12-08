package org.kevoree.registry.server.router;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.VersionHandler;

import static org.kevoree.registry.server.util.RequestHelper.get;

/**
 * API /ns
 * Created by leiko on 21/11/14.
 */
public class RegistryRouter extends AbstractRouter {

    private VersionHandler version;

    public RegistryRouter(Context context) {
        super(context);
        this.version = new VersionHandler(context);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        new PathHandler()
                .addPrefixPath("/version", get(version))
                .handleRequest(exchange);
    }
}
