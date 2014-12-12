package org.kevoree.registry.server.handler.namespace;

import com.eclipsesource.json.JsonObject;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.util.StatusCodes;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.util.ResponseHelper;

/**
 *
 * Created by leiko on 28/11/14.
 */
public class ShowNSHandler extends AbstractHandler {

    public ShowNSHandler(Context context) {
        super(context, false);
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        context.getTemplateManager().template(exchange, "namespace.html");
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        String fqn = exchange.getRelativePath();
        if (fqn.startsWith("/")) {
            fqn = fqn.substring(1);
        }
        Namespace ns = NamespaceDAO.getInstance(context.getEntityManagerFactory()).get(fqn);
        if (ns != null) {
            ResponseHelper.json(exchange, ns.toJson());
        } else {
            exchange.setResponseCode(StatusCodes.BAD_REQUEST);
            JsonObject response = new JsonObject();
            response.add("error", "Unable to find \""+fqn+"\"");
            ResponseHelper.json(exchange, response);
        }
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        handleHTML(exchange);
    }
}
