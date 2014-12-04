package org.kevoree.registry.server.router;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RedirectHandler;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.auth.*;
import org.kevoree.registry.server.oauth.google.Auth;

import static org.kevoree.registry.server.util.RequestHelper.get;

/**
 * API /auth router
 * Created by leiko on 24/11/14.
 */
public class AuthRouter extends AbstractRouter {

    private Auth googleAuth;

    public AuthRouter(Context context) {
        super(context);

        googleAuth = new Auth();
        googleAuth.responseType = "code";
        googleAuth.clientId = "16251752628-ai75vb50b3iuaofbuabiv9606khierps.apps.googleusercontent.com";
        googleAuth.scope = "https://www.googleapis.com/auth/userinfo.profile+https://www.googleapis.com/auth/userinfo.email";
        googleAuth.clientSecret = System.getProperty("google_oauth2_secret");
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        new PathHandler()
                .addPrefixPath("/signin", new SignInHandler(context))
                .addPrefixPath("/login", new LogInHandler(context))
                .addPrefixPath("/logout", get(new LogOutHandler()))
                .addPrefixPath("/gconnect", get(new GoogleConnectHandler(googleAuth)))
                .addPrefixPath("/gcallback", get(new GoogleCallbackHandler(context, googleAuth)))
                .addPrefixPath("/", new RedirectHandler("/"))
                .handleRequest(exchange);
    }
}