package org.kevoree.registry.server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
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

import java.util.List;

/**
 * Created by duke on 8/27/14.
 */
public class GetHandler implements HttpHandler {

    private KevoreeTransactionManager manager;

    public GetHandler(KevoreeTransactionManager manager) {
        this.manager = manager;
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        httpServerExchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");

        KevoreeTransaction trans = manager.createTransaction();
        try {
            String[] paths = httpServerExchange.getRequestPath().split("/");
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
            String acceptType = httpServerExchange.getRequestHeaders().get("Accept").getFirst();
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

                        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        httpServerExchange.getResponseSender().send(prunedModelSaved);

                    } else {
                        // send XMI back
                        String prunedModelSaved = tempTransaction.createXMISerializer().serialize(prunedRoot);
                        tempTransaction.close();
                        tempMemoryManager.close();

                        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/vnd.xmi+xml");
                        httpServerExchange.getResponseSender().send(prunedModelSaved);
                    }

                } else if (acceptType.contains("text/plain")) {
                    // send TRACES back
                    ModelPruner pruner = trans.createModelPruner();
                    TraceSequence prunedTraceSeq = pruner.prune(selected);

                    httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    httpServerExchange.getResponseSender().send(prunedTraceSeq.exportToString());

                } else {
                    // send HTML view of the model
                    httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    httpServerExchange.getResponseSender().send(Helper.generate(selected, httpServerExchange.getRelativePath()));
                }

            } else {
                if (acceptType.contains("text/html")) {
                    httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    httpServerExchange.getResponseSender().send(Helper.generate(selected, httpServerExchange.getRelativePath()));
                } else {
                    httpServerExchange.setResponseCode(404);
                    httpServerExchange.getResponseSender().close();
                }
            }
        } finally {
            trans.close();
        }
    }
}
