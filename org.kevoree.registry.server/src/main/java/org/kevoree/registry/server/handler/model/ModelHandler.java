package org.kevoree.registry.server.handler.model;

import freemarker.template.SimpleHash;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
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
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.template.TemplateManager;
import org.kevoree.registry.server.util.ModelHelper;

import java.util.List;

/**
 * Created by duke on 8/22/14.
 */
public class ModelHandler extends AbstractTemplateHandler {

    private KevoreeTransactionManager manager;

    public ModelHandler(TemplateManager tplManager, KevoreeTransactionManager manager) {
        super(tplManager);
        this.manager = manager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");

        KevoreeTransaction trans = manager.createTransaction();
        try {
            String[] paths = exchange.getRelativePath().split("/");
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
            HeaderValues acceptValues = exchange.getRequestHeaders().get(Headers.ACCEPT);
            if (acceptValues != null) {
                String acceptType = acceptValues.getFirst();
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
                            exchange.getResponseSender().close();
                            exchange.endExchange();

                        } else {
                            // send XMI back
                            String prunedModelSaved = tempTransaction.createXMISerializer().serialize(prunedRoot);
                            tempTransaction.close();
                            tempMemoryManager.close();

                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/vnd.xmi+xml");
                            exchange.getResponseSender().send(prunedModelSaved);
                            exchange.getResponseSender().close();
                            exchange.endExchange();
                        }

                    } else if (acceptType.contains("text/plain")) {
                        // send TRACES back
                        ModelPruner pruner = trans.createModelPruner();
                        TraceSequence prunedTraceSeq = pruner.prune(selected);

                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send(prunedTraceSeq.exportToString());
                        exchange.getResponseSender().close();
                        exchange.endExchange();

                    } else {
                        // send HTML view of the model
                        htmlResponse(exchange, selected);
                    }

                } else {
                    if (acceptType.contains("text/html")) {
                        htmlResponse(exchange, selected);
                    } else {
                        exchange.setResponseCode(StatusCodes.NOT_FOUND);
                        exchange.getResponseSender().close();
                        exchange.endExchange();
                    }
                }
            } else {
                htmlResponse(exchange, selected);
            }
        } finally {
            trans.close();
        }
    }

    private void htmlResponse(HttpServerExchange exchange, List<KMFContainer> selected)
            throws Exception {
        SimpleHash data = new SimpleHash();

        data.put("isEmpty", selected.isEmpty());
        data.put("relativePath", exchange.getRelativePath());
        data.put("previousPath", ModelHelper.generatePreviousPath(exchange.getRelativePath()));
        data.put("children", ModelHelper.generateChildren(selected, exchange.getRelativePath()));
        data.put("elements", ModelHelper.generateElements(selected));

        tplManager.template(exchange, data, "model.ftl");
    }
}
