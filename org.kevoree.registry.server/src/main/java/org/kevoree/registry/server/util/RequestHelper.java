package org.kevoree.registry.server.util;

import io.undertow.io.UndertowInputStream;
import io.undertow.server.HttpServerExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
