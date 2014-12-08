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
        json(exchange, json.toString());
    }

    /**
     * Sends the given String and closes the response sender.
     * This helper also set the response header Content-Type to "application/json"
     * @param exchange
     * @param jsonStr
     */
    public static void json(HttpServerExchange exchange, String jsonStr) {
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(jsonStr);
        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
    }

    /**
     * HTTP Response 200 OK
     * @param exchange
     */
    public static void ok(HttpServerExchange exchange) {
        exchange.setResponseCode(StatusCodes.OK);
        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
    }
}
