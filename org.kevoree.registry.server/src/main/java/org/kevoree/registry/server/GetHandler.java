package org.kevoree.registry.server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
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
            boolean jsonRequest = httpServerExchange.getQueryParameters().get("json") != null;
            boolean traceRequest = httpServerExchange.getQueryParameters().get("trace") != null;
            boolean xmiRequest = httpServerExchange.getQueryParameters().get("xmi") != null;
            if (jsonRequest || traceRequest || xmiRequest) {
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                if (selected.size() > 0) {
                    if (traceRequest) {
                        ModelPruner pruner = trans.createModelPruner();
                        TraceSequence prunedTraceSeq = pruner.prune(selected);
                        httpServerExchange.getResponseSender().send(prunedTraceSeq.exportToString());
                    }
                    if (jsonRequest) {
                        MemoryDataStore tempStore = new MemoryDataStore();
                        TransactionManager tempMemoryManager = new KevoreeTransactionManager(tempStore);
                        KevoreeTransaction tempTransaction = (KevoreeTransaction) tempMemoryManager.createTransaction();
                        //Create empty Root model to collect result
                        ContainerRoot prunedRoot = tempTransaction.createContainerRoot();
                        tempTransaction.root(prunedRoot);
                        ModelPruner pruner = tempTransaction.createModelPruner();
                        TraceSequence prunedTraceSeq = pruner.prune(selected);
                        prunedTraceSeq.applyOn(prunedRoot);
                        String prunedModelSaved = tempTransaction.createJSONSerializer().serialize(prunedRoot);
                        tempTransaction.close();
                        tempMemoryManager.close();
                        httpServerExchange.getResponseSender().send(prunedModelSaved);
                    }
                    if (xmiRequest) {
                        MemoryDataStore tempStore = new MemoryDataStore();
                        TransactionManager tempMemoryManager = new KevoreeTransactionManager(tempStore);
                        KevoreeTransaction tempTransaction = (KevoreeTransaction) tempMemoryManager.createTransaction();
                        //Create empty Root model to collect result
                        ContainerRoot prunedRoot = tempTransaction.createContainerRoot();
                        tempTransaction.root(prunedRoot);
                        ModelPruner pruner = tempTransaction.createModelPruner();
                        TraceSequence prunedTraceSeq = pruner.prune(selected);
                        prunedTraceSeq.applyOn(prunedRoot);
                        String prunedModelSaved = tempTransaction.createXMISerializer().serialize(prunedRoot);
                        tempTransaction.close();
                        tempMemoryManager.close();
                        httpServerExchange.getResponseSender().send(prunedModelSaved);
                    }
                }
            } else {
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                httpServerExchange.getResponseSender().send(Helper.generate(selected, httpServerExchange.getRelativePath()));
            }
            //httpServerExchange.endExchange();
        } finally {
            trans.close();
        }
    }
}
