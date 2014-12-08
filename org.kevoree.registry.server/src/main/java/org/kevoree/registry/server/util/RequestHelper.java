package org.kevoree.registry.server.util;

import io.undertow.io.UndertowInputStream;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.AllowedMethodsHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import org.kevoree.registry.server.handler.MethodAllowedHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by leiko on 17/11/14.
 */
public class RequestHelper {

    public static String getStringFrom(HttpServerExchange exchange) {
        InputStream inputStream = new UndertowInputStream(exchange);
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static HttpHandler get(HttpHandler next) {
        return new MethodAllowedHandler(Methods.GET, next);
    }

    public static HttpHandler post(final HttpHandler next) {
        return new MethodAllowedHandler(Methods.POST, next);
    }

    public static HttpHandler options(HttpHandler next) {
        return new MethodAllowedHandler(Methods.OPTIONS, next);
    }

    public static HttpHandler head(HttpHandler next) {
        return new MethodAllowedHandler(Methods.HEAD, next);
    }

    public static HttpHandler delete(HttpHandler next) {
        return new MethodAllowedHandler(Methods.DELETE, next);
    }
}
