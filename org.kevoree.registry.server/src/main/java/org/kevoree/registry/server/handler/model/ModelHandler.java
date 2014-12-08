package org.kevoree.registry.server.handler.model;

import com.eclipsesource.json.JsonObject;
import io.undertow.io.IoCallback;
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
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.util.ResponseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by duke on 8/22/14.
 */
public class ModelHandler extends AbstractHandler {

    public ModelHandler(Context context) {
        super(context, false);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        super.handleRequest(exchange);
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        context.getTemplateManager().template(exchange, "model.ftl");
    }

    @Override
    protected void handleJson(HttpServerExchange exchange) throws Exception {
        List<KMFContainer> selected = getModelElements(exchange);
        MemoryDataStore tempStore = new MemoryDataStore();
        TransactionManager tempMemoryManager = new KevoreeTransactionManager(tempStore);
        KevoreeTransaction tempTransaction = (KevoreeTransaction) tempMemoryManager.createTransaction();

        ContainerRoot prunedRoot = tempTransaction.createContainerRoot();
        tempTransaction.root(prunedRoot);
        ModelPruner pruner = tempTransaction.createModelPruner();
        TraceSequence prunedTraceSeq = pruner.prune(selected);
        prunedTraceSeq.applyOn(prunedRoot);

        String prunedModelSaved = tempTransaction.createJSONSerializer().serialize(prunedRoot);
        tempTransaction.close();
        tempMemoryManager.close();

        ResponseHelper.json(exchange, prunedModelSaved);
    }

    private void handleTextPlain(HttpServerExchange exchange) {
        List<KMFContainer> selected = getModelElements(exchange);
        TransactionManager tempMemoryManager = new KevoreeTransactionManager(new MemoryDataStore());
        KevoreeTransaction tempTransaction = (KevoreeTransaction) tempMemoryManager.createTransaction();

        ModelPruner pruner = tempTransaction.createModelPruner();
        TraceSequence prunedTraceSeq = pruner.prune(selected);

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send(prunedTraceSeq.exportToString());
        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
    }

    private void handleXMI(HttpServerExchange exchange) {
        List<KMFContainer> selected = getModelElements(exchange);
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

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/vnd.xmi+xml");
        exchange.getResponseSender().send(prunedModelSaved);
        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        HeaderValues acceptValues = exchange.getRequestHeaders().get(Headers.ACCEPT);
        if (acceptValues != null) {
            if (acceptValues.getFirst().startsWith("application/vnd.xmi+xml")) {
                handleXMI(exchange);

            } else if (acceptValues.getFirst().startsWith("text/plain")) {
                handleTextPlain(exchange);
            }
        } else {
            exchange.setResponseCode(StatusCodes.NOT_ACCEPTABLE);
            JsonObject response = new JsonObject();
            response.add("error", "Expecting text/{plain, html}, application/{json, vnd.xmi+xml}");
            ResponseHelper.json(exchange, response);
        }
    }

    private List<KMFContainer> getModelElements(HttpServerExchange exchange) {
        List<KMFContainer> selected = new ArrayList<KMFContainer>();
        KevoreeTransaction trans = context.getKevoreeTransactionManager().createTransaction();
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
            selected = trans.select(pathBuilder.toString());

        } finally {
            trans.close();
        }

        return selected;
    }
}
