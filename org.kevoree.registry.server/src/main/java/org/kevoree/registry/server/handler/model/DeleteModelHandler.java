package org.kevoree.registry.server.handler.model;

import com.eclipsesource.json.JsonObject;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.template.TemplateManager;
import org.kevoree.registry.server.util.ResponseHelper;

/**
 *
 * Created by leiko on 02/12/14.
 */
public class DeleteModelHandler extends AbstractHandler {

    public DeleteModelHandler(TemplateManager manager) {
        super(manager, true);
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        // TODO
        exchange.setResponseCode(StatusCodes.NOT_IMPLEMENTED);
        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        handleOther(exchange);
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        exchange.setResponseCode(StatusCodes.NOT_ACCEPTABLE);
        JsonObject response = new JsonObject();
        response.add("error", "Only accept application/json or text/html request");
        ResponseHelper.json(exchange, response);
    }
}
