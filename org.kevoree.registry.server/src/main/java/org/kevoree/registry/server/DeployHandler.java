package org.kevoree.registry.server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.kevoree.ContainerRoot;
import org.kevoree.factory.KevoreeTransaction;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.TransactionManager;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.trace.TraceSequence;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by duke on 8/27/14.
 */
public class DeployHandler implements HttpHandler {

    private ExecutorService dispatcher = Executors.newSingleThreadExecutor();

    private KevoreeTransactionManager manager;

    public DeployHandler(KevoreeTransactionManager manager) {
        this.manager = manager;
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        if (httpServerExchange.isInIoThread()) {
            httpServerExchange.dispatch(this);
            return;
        }

        final String payloadRec = Helper.getStringFrom(httpServerExchange);
        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        httpServerExchange.getResponseSender().send("ack");
        //httpServerExchange.endExchange();
        dispatcher.submit(new Runnable() {
            @Override
            public void run() {
                KevoreeTransaction currentTransaction = manager.createTransaction();
                try {
                    ContainerRoot currentRoot = (ContainerRoot) currentTransaction.lookup("/");
                    MemoryDataStore tempStore = new MemoryDataStore();
                    TransactionManager tempMemoryManager = new KevoreeTransactionManager(tempStore);
                    KevoreeTransaction tempTransaction = (KevoreeTransaction) tempMemoryManager.createTransaction();
                    List<KMFContainer> models = tempTransaction.createJSONLoader().loadModelFromString(payloadRec);
                    ModelCompare compare = currentTransaction.createModelCompare();
                    for (KMFContainer model : models) {
                        ContainerRoot newRootToCompare = tempTransaction.createContainerRoot().withGenerated_KMF_ID("0");
                        TraceSequence seq = compare.merge(newRootToCompare, model);
                        seq.applyOn(currentRoot);
                    }
                    tempTransaction.close();
                    tempMemoryManager.close();
                    currentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    currentTransaction.close();
                }
            }
        });
    }


}
