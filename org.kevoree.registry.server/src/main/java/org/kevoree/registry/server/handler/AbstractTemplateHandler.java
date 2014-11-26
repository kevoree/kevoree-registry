package org.kevoree.registry.server.handler;

import io.undertow.server.HttpHandler;
import org.kevoree.registry.server.template.TemplateManager;

/**
 * Created by leiko on 25/11/14.
 */
public abstract class AbstractTemplateHandler implements HttpHandler {

    protected TemplateManager tplManager;

    public AbstractTemplateHandler(TemplateManager manager) {
        this.tplManager = manager;
    }
}
