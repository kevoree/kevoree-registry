package org.kevoree.registry.server.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

/**
 *
 * Created by leiko on 05/12/14.
 */
public class MethodAllowedHandler implements HttpHandler {

    private HttpString allowedMethod;
    private HttpHandler next;

    public MethodAllowedHandler(HttpString method, HttpHandler next) {
        this.allowedMethod = method;
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpString method = exchange.getRequestMethod();
        if (allowedMethod.equals(method)) {
            next.handleRequest(exchange);
        } else {
            HeaderValues acceptValues = exchange.getRequestHeaders().get(Headers.ACCEPT);

            if (acceptValues == null || !acceptValues.getFirst().startsWith("text/html")) {
                exchange.setResponseCode(StatusCodes.METHOD_NOT_ALLOWED);
                exchange.endExchange();
            } else {
                new RedirectHandler("/").handleRequest(exchange);
            }
        }
    }
}
