package org.kevoree.registry.server.handler;

import freemarker.template.*;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

import org.kevoree.ContainerRoot;
import org.kevoree.factory.KevoreeTransaction;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.ModelPruner;
import org.kevoree.modeling.api.TransactionManager;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.registry.server.util.ModelHelper;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.StringWriter;
import java.util.List;

/**
 * Created by duke on 8/27/14.
 */
public class TimestampHandler implements HttpHandler {

    private KevoreeTransactionManager manager;

    public TimestampHandler(KevoreeTransactionManager manager) {
        this.manager = manager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        String accept = exchange.getRequestHeaders().get(Headers.ACCEPT).getFirst();
        if (accept.startsWith("text/html")) {
            exchange.setResponseCode(StatusCodes.FOUND);
            exchange.getResponseHeaders().put(Headers.LOCATION, "/");
            exchange.endExchange();
        } else {
            KevoreeTransaction trans = manager.createTransaction();
            try {
            	ContainerRoot model = (ContainerRoot) trans.lookup("/");
            	JsonObject resp = new JsonObject();
            	resp.add("value", Long.parseLong(model.getGenerated_KMF_ID()));
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(resp.toString());
            } finally {
                trans.close();
            }
        }
    }
}
