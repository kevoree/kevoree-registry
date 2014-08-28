package org.kevoree.registry.server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
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

    public MainHandler(KevoreeTransactionManager ma) {
        this.manager = ma;
        this.deployHandler = new DeployHandler(manager);
        this.getHandler = new GetHandler(manager);
        this.searchHandler = new SearchHandler(manager);
    }

    @Override
    public void handleRequest(final HttpServerExchange httpServerExchange) throws Exception {
        if (httpServerExchange.getRequestPath().equals("/favicon.ico")) {
            httpServerExchange.endExchange();
        } else {
            if (httpServerExchange.getRequestMethod().equals(HttpString.tryFromString("GET"))) {
                getHandler.handleRequest(httpServerExchange);
            } else {
                if (httpServerExchange.getRequestMethod().equals(HttpString.tryFromString("POST"))) {
                    if (httpServerExchange.getRelativePath().equals("/deploy")) {
                        deployHandler.handleRequest(httpServerExchange);
                    } else {
                        searchHandler.handleRequest(httpServerExchange);
                    }
                }
            }

        }
    }


}
