package org.kevoree.registry.server.handler;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionCookieConfig;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.registry.server.handler.model.ModelHandler;
import org.kevoree.registry.server.handler.model.SearchModelHandler;
import org.kevoree.registry.server.manager.DbSessionManager;
import org.kevoree.registry.server.router.AuthRouter;
import org.kevoree.registry.server.router.NamespaceRouter;
import org.kevoree.registry.server.router.UserRouter;
import org.kevoree.registry.server.template.TemplateManager;
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

    public KevoreeRegistryHandler(KevoreeTransactionManager manager, KevoreeFactory factory) throws IOException {
        Configuration conf = new Configuration();
        conf.setClassForTemplateLoading(getClass(), "/WEB-INF/templates");
        conf.setDefaultEncoding("UTF-8");
        conf.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        TemplateManager tplManager = new TemplateManager(conf, "layout.ftl");
        tplManager.putLayoutData("version", factory.getVersion());

        this.sessionAttachmentHandler = new SessionAttachmentHandler(
                new SessionHandler(
                        new CSRFHandler(tplManager, new PathHandler()
                                .addPrefixPath("/!/auth", new AuthRouter(tplManager))
                                .addPrefixPath("/!/user", new UserRouter(tplManager))
                                .addPrefixPath("/!/ns", new NamespaceRouter(tplManager))
                                .addPrefixPath("/!/search", new SearchModelHandler(tplManager))
                                .addPrefixPath("/!/static", get(new StaticHandler()))
                                .addPrefixPath("/", new ModelHandler(tplManager, manager))
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
