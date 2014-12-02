package org.kevoree.registry.server.router;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.handler.namespace.AddNSHandler;
import org.kevoree.registry.server.handler.namespace.DeleteNSHandler;
import org.kevoree.registry.server.handler.namespace.GetNSHandler;
import org.kevoree.registry.server.handler.namespace.LeaveNSHandler;
import org.kevoree.registry.server.template.TemplateManager;
import org.kevoree.registry.server.util.RequestHelper;

/**
 * API /ns
 * Created by leiko on 21/11/14.
 */
public class NamespaceRouter extends AbstractTemplateHandler {

    private DeleteNSHandler delete;
    private LeaveNSHandler leave;
    private AddNSHandler add;
    private GetNSHandler get;

    public NamespaceRouter(TemplateManager manager) {
        super(manager);
        delete = new DeleteNSHandler(manager);
        leave = new LeaveNSHandler(manager);
        add = new AddNSHandler(manager);
        get = new GetNSHandler(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        new PathHandler()
                .addPrefixPath("/add", RequestHelper.post(add))
                .addPrefixPath("/delete", RequestHelper.post(delete))
                .addPrefixPath("/leave", RequestHelper.post(leave))
                .addPrefixPath("/", RequestHelper.get(get))
                .handleRequest(exchange);
    }
}
