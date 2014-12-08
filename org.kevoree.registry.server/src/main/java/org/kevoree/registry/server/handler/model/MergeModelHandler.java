package org.kevoree.registry.server.handler.model;

import com.eclipsesource.json.JsonObject;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.kevoree.ContainerRoot;
import org.kevoree.factory.KevoreeTransaction;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.ModelLoader;
import org.kevoree.modeling.api.TransactionManager;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.registry.server.Context;
import org.kevoree.registry.server.handler.AbstractHandler;
import org.kevoree.registry.server.util.RequestHelper;
import org.kevoree.registry.server.util.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * Created by leiko on 02/12/14.
 */
public class MergeModelHandler extends AbstractHandler {

    private static final Logger log = LoggerFactory.getLogger(MergeModelHandler.class.getSimpleName());

    public MergeModelHandler(Context context) {
        super(context, true);
    }

    @Override
    protected void handleJson(final HttpServerExchange exchange) throws Exception {
        final String payloadRec = RequestHelper.getStringFrom(exchange);
        HeaderValues contentTypeValues = exchange.getRequestHeaders().get(Headers.CONTENT_TYPE);
        if (contentTypeValues != null) {
            String contentType = contentTypeValues.getFirst();
            final boolean isJSON = contentType.contains("application/json");
            final boolean isXMI = contentType.contains("application/vnd.xmi+xml");
            final boolean isTrace = contentType.contains("text/plain");

            if (!isJSON && !isXMI && !isTrace) {
                exchange.setResponseCode(StatusCodes.UNSUPPORTED_MEDIA_TYPE);
                JsonObject response = new JsonObject();
                response.add("error", "Unsupported Content-Type (expected application/{json, vnd.xmi+xml} or text/plain");
                ResponseHelper.json(exchange, response);
            }

            exchange.dispatch(new Runnable() {
                @Override
                public void run() {
                    KevoreeTransaction currentTransaction = context.getKevoreeTransactionManager().createTransaction();
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
                            if (models != null) {
                                for (KMFContainer model : models) {
                                    ContainerRoot newRootToCompare = tempTransaction.createContainerRoot().withGenerated_KMF_ID("0");
                                    TraceSequence seq = compare.merge(newRootToCompare, model);
                                    if (currentRoot != null) {
                                        seq.applyOn(currentRoot);
                                    }
                                }
                            }

                        } else if (isTrace) {
                            TraceSequence ts = new TraceSequence(context.getKevoreeFactory());
                            ts.populateFromString(payloadRec);
                            if (currentRoot != null) {
                                ts.applyOn(currentRoot);
                            }
                        }

                        tempTransaction.close();
                        tempMemoryManager.close();
                        currentTransaction.commit();

                        exchange.setResponseCode(StatusCodes.CREATED);
                        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                    } catch (Exception e) {
                        log.error("500 - Internal Server Error - {}", exchange, e.getMessage());
                        log.debug("Caught exception", e);
                        exchange.setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
                        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
                    } finally {
                        currentTransaction.close();
                    }
                }
            });
        } else {
            exchange.setResponseCode(StatusCodes.BAD_REQUEST);
            JsonObject response = new JsonObject();
            response.add("error", "Expect Content-Type to be set to application/{json, vnd.xmi+xml} or text/plain");
            ResponseHelper.json(exchange, response);
        }
    }

    @Override
    protected void handleHTML(HttpServerExchange exchange) throws Exception {
        handleOther(exchange);
    }

    @Override
    protected void handleOther(HttpServerExchange exchange) throws Exception {
        exchange.setResponseCode(StatusCodes.NOT_ACCEPTABLE);
        JsonObject response = new JsonObject();
        response.add("error", "Only accept application/json or text/html response");
        ResponseHelper.json(exchange, response);
    }
}
