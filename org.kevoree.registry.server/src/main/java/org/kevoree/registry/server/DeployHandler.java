package org.kevoree.registry.server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.jetbrains.annotations.NotNull;
import org.kevoree.ContainerRoot;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.factory.KevoreeTransaction;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.ModelLoader;
import org.kevoree.modeling.api.Transaction;
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
    public void handleRequest(final HttpServerExchange httpServerExchange) throws Exception {
        if (httpServerExchange.isInIoThread()) {
            httpServerExchange.dispatch(this);
            return;
        }

        final String payloadRec = Helper.getStringFrom(httpServerExchange);
        String contentType = httpServerExchange.getRequestHeaders().get(Headers.CONTENT_TYPE).getFirst();
        final boolean isJSON = contentType.contains("application/json");
        final boolean isXMI = contentType.contains("application/vnd.xmi+xml");
        final boolean isTrace = contentType.contains("text/plain");
        if (isJSON || isXMI || isTrace) {
            httpServerExchange.setResponseCode(201);
            httpServerExchange.getResponseSender().close();
        } else {
            httpServerExchange.setResponseCode(406);
            httpServerExchange.getResponseSender().send("Unknown model mime type ("+contentType+")");
        }

        dispatcher.submit(new Runnable() {
            @Override
            public void run() {
                KevoreeTransaction currentTransaction = manager.createTransaction();
                try {
                    ContainerRoot currentRoot = (ContainerRoot) currentTransaction.lookup("/");
                    MemoryDataStore tempStore = new MemoryDataStore();
                    TransactionManager tempMemoryManager = new KevoreeTransactionManager(tempStore);
                    KevoreeTransaction tempTransaction = (KevoreeTransaction) tempMemoryManager.createTransaction();


                    if (isJSON || isXMI) {
                        ModelLoader loader;
                        if (isJSON) {
                            loader = tempTransaction.createJSONLoader();
                        } else {
                            loader = tempTransaction.createXMILoader();
                        }
                        List<KMFContainer> models = loader.loadModelFromString(payloadRec);
                        ModelCompare compare = currentTransaction.createModelCompare();
                        for (KMFContainer model : models) {
                            ContainerRoot newRootToCompare = tempTransaction.createContainerRoot().withGenerated_KMF_ID("0");
                            TraceSequence seq = compare.merge(newRootToCompare, model);
                            seq.applyOn(currentRoot);
                        }

                    } else if (isTrace) {
                        TraceSequence ts = new TraceSequence(new DefaultKevoreeFactory(new MemoryDataStore()) {
                            @NotNull
                            @Override
                            public Transaction getOriginTransaction() {
                                return null;
                            }
                        });
                        ts.populateFromString(payloadRec);
                        ts.applyOn(currentRoot);
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
