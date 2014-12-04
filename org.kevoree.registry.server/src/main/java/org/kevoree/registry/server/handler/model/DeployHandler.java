package org.kevoree.registry.server.handler.model;

import io.undertow.io.IoCallback;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
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
import org.kevoree.registry.server.util.RequestHelper;

import java.util.List;

/**
 * Created by duke on 8/27/14.
 */
public class DeployHandler implements HttpHandler {

    private KevoreeTransactionManager manager;
    private KevoreeFactory factory;

    public DeployHandler(KevoreeTransactionManager manager, KevoreeFactory factory) {
        this.manager = manager;
        this.factory = factory;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        final String payloadRec = RequestHelper.getStringFrom(exchange);
        HeaderValues contentTypeValues = exchange.getRequestHeaders().get(Headers.CONTENT_TYPE);
        if (contentTypeValues != null) {
            String contentType = contentTypeValues.getFirst();
            final boolean isJSON = contentType.contains("application/json");
            final boolean isXMI = contentType.contains("application/vnd.xmi+xml");
            final boolean isTrace = contentType.contains("text/plain");

            if (!isJSON && !isXMI && !isTrace) {
                exchange.setResponseCode(406);
                exchange.getResponseSender().send("Unknown model mime type ("+contentType+")");
            }

            if (exchange.isInIoThread()) {
                exchange.dispatch(new Runnable() {
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
                                TraceSequence ts = new TraceSequence(factory);
                                ts.populateFromString(payloadRec);
                                ts.applyOn(currentRoot);
                            }

                            tempTransaction.close();
                            tempMemoryManager.close();
                            currentTransaction.commit();

                            exchange.setResponseCode(201);
                            exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            exchange.setResponseCode(500);
                            exchange.getResponseSender().send("Server error");
                            exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                        } finally {
                            currentTransaction.close();
                        }
                    }
                });
            }
        } else {
            exchange.setResponseCode(406);
            exchange.getResponseSender().send("Unknown model mime type (Content-Type not set)");
        }
    }
}
