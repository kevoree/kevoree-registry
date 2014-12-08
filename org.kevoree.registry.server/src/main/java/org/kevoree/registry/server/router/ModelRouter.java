package org.kevoree.registry.server.router;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.model.DeleteModelHandler;
import org.kevoree.registry.server.handler.model.MergeModelHandler;
import org.kevoree.registry.server.handler.model.SearchModelHandler;

import static org.kevoree.registry.server.util.RequestHelper.post;
import static org.kevoree.registry.server.util.RequestHelper.get;

/**
 * API /ns
 * Created by leiko on 21/11/14.
 */
public class ModelRouter extends AbstractRouter {

    private MergeModelHandler merge;
    private DeleteModelHandler delete;
    private SearchModelHandler search;

    public ModelRouter(Context context) {
        super(context);
        this.merge = new MergeModelHandler(context);
        this.delete = new DeleteModelHandler(context);
        this.search = new SearchModelHandler(context);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        new PathHandler()
                .addPrefixPath("/merge", post(merge))
                .addPrefixPath("/delete", post(delete))
                .addPrefixPath("/search", get(search))
                .handleRequest(exchange);
    }
}
