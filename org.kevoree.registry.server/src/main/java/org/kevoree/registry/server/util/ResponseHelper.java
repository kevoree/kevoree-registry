package org.kevoree.registry.server.util;

import com.eclipsesource.json.JsonValue;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

/**
 * Created by leiko on 28/11/14.
 */
public class ResponseHelper {

    /**
     * Sends the given JsonValue as a string and closes the response sender.
     * This helper also set the response header Content-Type to "application/json"
     * @param exchange
     * @param json
     */
    public static void json(HttpServerExchange exchange, JsonValue json) {
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(json.toString());
        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
    }

    public static void ok(HttpServerExchange exchange) {
        exchange.setResponseCode(StatusCodes.OK);
        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
    }
}
