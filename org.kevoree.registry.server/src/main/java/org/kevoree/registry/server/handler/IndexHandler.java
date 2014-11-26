package org.kevoree.registry.server.handler;

import freemarker.template.SimpleHash;
import io.undertow.server.HttpServerExchange;
import org.kevoree.registry.server.template.TemplateManager;

/**
 * Created by leiko on 24/11/14.
 */
public class IndexHandler extends AbstractTemplateHandler {

    public IndexHandler(TemplateManager manager) {
        super(manager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        SimpleHash data = new SimpleHash();
        tplManager.template(exchange, data, "index.ftl");
    }
}
