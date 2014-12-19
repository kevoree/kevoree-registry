package org.kevoree.registry.server.handler.auth;

import com.eclipsesource.json.JsonObject;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.util.ResponseHelper;

/**
 * Created by leiko on 17/12/14.
 */
public class AuthHandler extends AbstractHandler {

    public AuthHandler(Context context) {
        super(context, false);
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        new RedirectHandler("/").handleRequest(exchange);
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        ResponseHelper.ok(exchange);
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        exchange.setResponseCode(StatusCodes.NOT_ACCEPTABLE);
        JsonObject response = new JsonObject();
        response.add("error", "Only accept application/json or text/html request");
        ResponseHelper.json(exchange, response);
    }
}
