package org.kevoree.registry.server.handler;

import freemarker.template.Configuration;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.HttpString;
import org.kevoree.factory.KevoreeTransactionManager;

/**
 * Created by duke on 8/22/14.
 */
public class MainHandler implements HttpHandler {

    private KevoreeTransactionManager manager;

    private DeployHandler deployHandler;
    private GetHandler getHandler;
    private SearchHandler searchHandler;
    private HttpHandler staticHandler;

    public MainHandler(KevoreeTransactionManager ma, Configuration config, String kevoreeVersion) {
        this.manager = ma;
        this.deployHandler = new DeployHandler(manager);
        this.getHandler = new GetHandler(manager, config, kevoreeVersion);
        this.searchHandler = new SearchHandler(manager);
        this.staticHandler = new ResourceHandler(new ClassPathResourceManager(getClass().getClassLoader()));
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        if (exchange.getRequestMethod().equals(HttpString.tryFromString("GET"))) {
            if (exchange.getRequestPath().startsWith("/static/")) {
                String path = exchange.getRelativePath();
                exchange.setRelativePath(path.replace("/static/", "/WEB-INF/"));
                staticHandler.handleRequest(exchange);
            } else {
                getHandler.handleRequest(exchange);
            }
        } else if (exchange.getRequestMethod().equals(HttpString.tryFromString("POST"))) {
            if (exchange.getRelativePath().equals("/deploy")) {
                deployHandler.handleRequest(exchange);
            } else {
                searchHandler.handleRequest(exchange);
            }
        } else if (exchange.getRequestMethod().equals(HttpString.tryFromString("OPTIONS"))) {
            searchHandler.handleRequest(exchange);
        }
    }
}
