package org.kevoree.registry.server.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

import java.io.File;
import java.io.InputStream;

/**
 * Created by leiko on 17/11/14.
 */
public class StaticHandler implements HttpHandler {

    private HttpHandler resHandler;

    public StaticHandler() {
        this.resHandler = new ResourceHandler(new ClassPathResourceManager(getClass().getClassLoader()));
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String path = exchange.getRelativePath();
        if (!path.startsWith("/")) { path = "/" + path; }
        exchange.setRelativePath("/WEB-INF/static" + path);
        this.resHandler.handleRequest(exchange);
    }
}
