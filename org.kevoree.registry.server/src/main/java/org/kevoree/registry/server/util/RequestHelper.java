package org.kevoree.registry.server.util;

import io.undertow.io.UndertowInputStream;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.AllowedMethodsHandler;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
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
        Set<HttpString> methods = new HashSet<HttpString>();
        methods.add(Methods.GET);
        return new AllowedMethodsHandler(next, methods);
    }

    public static HttpHandler post(HttpHandler next) {
        Set<HttpString> methods = new HashSet<HttpString>();
        methods.add(Methods.POST);
        return new AllowedMethodsHandler(next, methods);
    }

    public static HttpHandler options(HttpHandler next) {
        Set<HttpString> methods = new HashSet<HttpString>();
        methods.add(Methods.OPTIONS);
        return new AllowedMethodsHandler(next, methods);
    }

    public static HttpHandler delete(HttpHandler next) {
        Set<HttpString> methods = new HashSet<HttpString>();
        methods.add(Methods.DELETE);
        return new AllowedMethodsHandler(next, methods);
    }
}
