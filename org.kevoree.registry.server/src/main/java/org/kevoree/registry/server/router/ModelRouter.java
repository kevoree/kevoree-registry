package org.kevoree.registry.server.router;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.handler.model.DeleteModelHandler;
import org.kevoree.registry.server.handler.model.MergeModelHandler;
import org.kevoree.registry.server.template.TemplateManager;

import static org.kevoree.registry.server.util.RequestHelper.post;

/**
 * API /ns
 * Created by leiko on 21/11/14.
 */
public class ModelRouter extends AbstractTemplateHandler {

    private MergeModelHandler merge;
    private DeleteModelHandler delete;

    public ModelRouter(TemplateManager manager) {
        super(manager);
        this.merge = new MergeModelHandler(manager);
        this.delete = new DeleteModelHandler(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        new PathHandler()
                .addPrefixPath("/merge", post(merge))
                .addPrefixPath("/delete", post(delete))
                .handleRequest(exchange);
    }
}
