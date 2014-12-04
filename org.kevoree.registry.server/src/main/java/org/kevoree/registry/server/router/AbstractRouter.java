package org.kevoree.registry.server.router;

import io.undertow.server.HttpHandler;
import org.kevoree.registry.server.Context;

/**
 * Created by leiko on 04/12/14.
 */
public abstract class AbstractRouter implements HttpHandler {

    protected final Context context;

    public AbstractRouter(Context context) {
        this.context = context;
    }
}
