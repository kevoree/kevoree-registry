package org.kevoree.registry.server;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duke on 8/27/14.
 */
public class SearchHandler implements HttpHandler {

    private KevoreeTransactionManager manager;

    public SearchHandler(KevoreeTransactionManager manager) {
        this.manager = manager;
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        final String payloadRec = Helper.getStringFrom(httpServerExchange);
        JsonArray jsonArray = JsonArray.readFrom(payloadRec);
        KevoreeTransaction transaction = this.manager.createTransaction();
        try {
            List<KMFContainer> selected = new ArrayList<KMFContainer>();
            for (JsonValue value : jsonArray) {
                selected.addAll(transaction.select(value.asString()));
            }
            boolean jsonRequest = httpServerExchange.getQueryParameters().get("json") != null;
            boolean traceRequest = httpServerExchange.getQueryParameters().get("trace") != null;
            httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            if (selected.size() > 0) {
                if (traceRequest) {
                    ModelPruner pruner = transaction.createModelPruner();
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
            }
        } finally {
            transaction.close();
        }
    }
}
