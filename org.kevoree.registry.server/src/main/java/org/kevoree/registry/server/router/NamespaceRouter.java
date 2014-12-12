package org.kevoree.registry.server.router;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RedirectHandler;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.namespace.*;
import org.kevoree.registry.server.util.RequestHelper;

/**
 * API /ns
 * Created by leiko on 21/11/14.
 */
public class NamespaceRouter extends AbstractRouter {

    private DeleteNSHandler delete;
    private LeaveNSHandler leave;
    private RemoveMemberNSHandler remove;
    private RegisterMemberNSHandler register;
    private AddNSHandler add;
    private ShowNSHandler show;

    public NamespaceRouter(Context context) {
        super(context);
        delete = new DeleteNSHandler(context);
        leave = new LeaveNSHandler(context);
        remove = new RemoveMemberNSHandler(context);
        register = new RegisterMemberNSHandler(context);
        add = new AddNSHandler(context);
        show = new ShowNSHandler(context);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        new PathHandler()
                .addPrefixPath("/add", RequestHelper.post(add))
                .addPrefixPath("/delete", RequestHelper.post(delete))
                .addPrefixPath("/leave", RequestHelper.post(leave))
                .addPrefixPath("/remove", RequestHelper.post(remove))
                .addPrefixPath("/register", RequestHelper.post(register))
                .addPrefixPath("/show", RequestHelper.get(show))
                .addPrefixPath("/", new RedirectHandler("/"))
                .handleRequest(exchange);
    }
}
