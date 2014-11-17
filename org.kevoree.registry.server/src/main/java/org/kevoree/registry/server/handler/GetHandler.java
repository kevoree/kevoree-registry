package org.kevoree.registry.server.handler;

import freemarker.template.*;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.kevoree.ContainerRoot;
import org.kevoree.factory.KevoreeTransaction;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.ModelPruner;
import org.kevoree.modeling.api.TransactionManager;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.registry.server.util.ModelHelper;

import java.io.StringWriter;
import java.util.List;

/**
 * Created by duke on 8/27/14.
 */
public class GetHandler implements HttpHandler {

    private KevoreeTransactionManager manager;
    private Configuration config;
    private String kevoreeVersion;

    public GetHandler(KevoreeTransactionManager manager, Configuration config, String kevoreeVersion) {
        this.manager = manager;
        this.config = config;
        this.kevoreeVersion = kevoreeVersion;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");

        KevoreeTransaction trans = manager.createTransaction();
        try {
            String[] paths = exchange.getRequestPath().split("/");
            StringBuilder pathBuilder = new StringBuilder();
            for (String path : paths) {
                if (!path.equals("")) {
                    pathBuilder.append("/");
                    pathBuilder.append("*[");
                    pathBuilder.append(path);
                    pathBuilder.append("]");
                }
            }
            if (pathBuilder.length() == 0) {
                pathBuilder.append("/");
            }
            List<KMFContainer> selected = trans.select(pathBuilder.toString());
            String acceptType = exchange.getRequestHeaders().get("Accept").getFirst();
            if (selected.size() > 0) {
                if (acceptType.contains("application/json") || acceptType.contains("application/vnd.xmi+xml")) {
                    MemoryDataStore tempStore = new MemoryDataStore();
                    TransactionManager tempMemoryManager = new KevoreeTransactionManager(tempStore);
                    KevoreeTransaction tempTransaction = (KevoreeTransaction) tempMemoryManager.createTransaction();
                    //Create empty Root model to collect result
                    ContainerRoot prunedRoot = tempTransaction.createContainerRoot();
                    tempTransaction.root(prunedRoot);
                    ModelPruner pruner = tempTransaction.createModelPruner();
                    TraceSequence prunedTraceSeq = pruner.prune(selected);
                    prunedTraceSeq.applyOn(prunedRoot);

                    if (acceptType.contains("application/json")) {
                        // send JSON back
                        String prunedModelSaved = tempTransaction.createJSONSerializer().serialize(prunedRoot);
                        tempTransaction.close();
                        tempMemoryManager.close();

                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send(prunedModelSaved);

                    } else {
                        // send XMI back
                        String prunedModelSaved = tempTransaction.createXMISerializer().serialize(prunedRoot);
                        tempTransaction.close();
                        tempMemoryManager.close();

                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/vnd.xmi+xml");
                        exchange.getResponseSender().send(prunedModelSaved);
                    }

                } else if (acceptType.contains("text/plain")) {
                    // send TRACES back
                    ModelPruner pruner = trans.createModelPruner();
                    TraceSequence prunedTraceSeq = pruner.prune(selected);

                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    exchange.getResponseSender().send(prunedTraceSeq.exportToString());

                } else {
                    // send HTML view of the model
                    htmlResponse(exchange, selected, config);
                }

            } else {
                if (acceptType.contains("text/html")) {
                    htmlResponse(exchange, selected, config);
                } else {
                    exchange.setResponseCode(404);
                    exchange.getResponseSender().close();
                }
            }
        } finally {
            trans.close();
        }
    }

    private void htmlResponse(HttpServerExchange exchange, List<KMFContainer> selected, Configuration config)
            throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");

        SimpleHash root = new SimpleHash();
        root.put("version", kevoreeVersion);
        root.put("previousPath", ModelHelper.generatePreviousPath(exchange.getRelativePath()));
        root.put("children", ModelHelper.generateChildren(selected, exchange.getRelativePath()));
        root.put("elements", ModelHelper.generateElements(selected));

        Template tpl = config.getTemplate("index.ftl");
        StringWriter writer = new StringWriter();
        tpl.process(root, writer);
        exchange.getResponseSender().send(writer.toString());
    }
}
