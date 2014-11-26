package org.kevoree.registry.server.router;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RedirectHandler;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.handler.namespace.AddNSHandler;
import org.kevoree.registry.server.handler.namespace.DeleteNSHandler;
import org.kevoree.registry.server.handler.namespace.LeaveNSHandler;
import org.kevoree.registry.server.template.TemplateManager;

import static org.kevoree.registry.server.util.RequestHelper.get;
import static org.kevoree.registry.server.util.RequestHelper.post;

/**
 * API /ns
 * Created by leiko on 21/11/14.
 */
public class NamespaceRouter extends AbstractTemplateHandler {

    private DeleteNSHandler delete;
    private LeaveNSHandler leave;
    private AddNSHandler add;

    public NamespaceRouter(TemplateManager manager) {
        super(manager);
        delete = new DeleteNSHandler();
        leave = new LeaveNSHandler();
        add = new AddNSHandler();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        new PathHandler()
                .addPrefixPath("/add", post(add))
                .addPrefixPath("/delete", get(delete))
                .addPrefixPath("/leave", get(leave))
                .addPrefixPath("/", new RedirectHandler("/"))
                .handleRequest(exchange);
    }
}
