package org.kevoree.registry.server.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionCookieConfig;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.model.ModelHandler;
import org.kevoree.registry.server.handler.model.SearchModelHandler;
import org.kevoree.registry.server.manager.DbSessionManager;
import org.kevoree.registry.server.router.AuthRouter;
import org.kevoree.registry.server.router.ModelRouter;
import org.kevoree.registry.server.router.NamespaceRouter;
import org.kevoree.registry.server.router.UserRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.kevoree.registry.server.util.RequestHelper.get;

/**
 * Kevoree Registry main handler
 * Created by leiko on 24/11/14.
 */
public class KevoreeRegistryHandler implements HttpHandler {

    private final Logger log = LoggerFactory.getLogger(KevoreeRegistryHandler.class.getSimpleName());

    private HttpHandler sessionAttachmentHandler;
    private final Context context;

    public KevoreeRegistryHandler(Context context)
            throws IOException {
        this.context = context;

        this.sessionAttachmentHandler = new SessionAttachmentHandler(
                new SessionHandler(context,
                        new CSRFHandler(context, new PathHandler()
                                .addPrefixPath("/!/model", new ModelRouter(context))
                                .addPrefixPath("/!/auth", new AuthRouter(context))
                                .addPrefixPath("/!/user", new UserRouter(context))
                                .addPrefixPath("/!/ns", new NamespaceRouter(context))
                                .addPrefixPath("/!/search", new SearchModelHandler(context))
                                .addPrefixPath("/!/static", get(new StaticHandler()))
                                .addPrefixPath("/", new ModelHandler(context))
                        )),
                new DbSessionManager("kevoree_registry"),
                new SessionCookieConfig()
        );
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
        } else {
            log.debug("{}", exchange);
            sessionAttachmentHandler.handleRequest(exchange);
        }
    }
}
