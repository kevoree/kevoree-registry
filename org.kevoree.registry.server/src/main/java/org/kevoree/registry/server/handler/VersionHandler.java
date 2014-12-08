package org.kevoree.registry.server.handler;

import com.eclipsesource.json.JsonObject;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.util.ResponseHelper;

/**
 * Created by leiko on 08/12/14.
 */
public class VersionHandler implements HttpHandler {

    private Context context;

    public VersionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        JsonObject response = new JsonObject();
        response.add("version", context.getKevoreeFactory().getVersion());
        ResponseHelper.json(exchange, response);
    }
}
