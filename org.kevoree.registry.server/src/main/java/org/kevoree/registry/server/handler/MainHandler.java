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
    private TimestampHandler timestampHandler;

    public MainHandler(KevoreeTransactionManager ma, Configuration config, String kevoreeVersion) {
        this.manager = ma;
        this.deployHandler = new DeployHandler(manager);
        this.getHandler = new GetHandler(manager, config, kevoreeVersion);
        this.searchHandler = new SearchHandler(manager);
        this.timestampHandler = new TimestampHandler(manager);
        this.staticHandler = new ResourceHandler(new ClassPathResourceManager(getClass().getClassLoader()));
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        if (exchange.getRequestMethod().equals(HttpString.tryFromString("GET"))) {
            if (exchange.getRequestPath().startsWith("/_/")) {
            	// XXX if someone creates a package called "_" then he is doomed
            	String path = exchange.getRelativePath();
                if (path.startsWith("/_/static/")) {
                	exchange.setRelativePath(path.replace("/_/static/", "/WEB-INF/"));
                    staticHandler.handleRequest(exchange);
                } else if (path.startsWith("/_/timestamp")) {
                	timestampHandler.handleRequest(exchange);
                }
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
