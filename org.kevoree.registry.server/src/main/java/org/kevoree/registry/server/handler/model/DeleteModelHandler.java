package org.kevoree.registry.server.handler.model;

import com.eclipsesource.json.JsonObject;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
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
import org.kevoree.registry.server.handler.SessionHandler;
import org.kevoree.registry.server.model.User;
import org.kevoree.registry.server.util.ModelHelper;
import org.kevoree.registry.server.util.RequestHelper;
import org.kevoree.registry.server.util.ResponseHelper;
import org.kevoree.registry.server.util.TraceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.util.List;

/**
 *
 * Created by leiko on 02/12/14.
 */
public class DeleteModelHandler extends AbstractHandler {

    private static final Logger log = LoggerFactory.getLogger(DeleteModelHandler.class.getSimpleName());

    public DeleteModelHandler(Context context) {
        super(context, true);
    }

    @Override
    protected void handleJson(final HttpServerExchange exchange) throws Exception {
        HeaderValues contentTypeValues = exchange.getRequestHeaders().get(Headers.CONTENT_TYPE);
        if (contentTypeValues != null) {
            String contentType = contentTypeValues.getFirst();
            final boolean isJSON = contentType.contains("application/json");
            final boolean isXMI = contentType.contains("application/vnd.xmi+xml");

            if (!isJSON && !isXMI) {
                exchange.setResponseCode(StatusCodes.UNSUPPORTED_MEDIA_TYPE);
                JsonObject response = new JsonObject();
                response.add("error", "Unsupported Content-Type (expected application/{json, vnd.xmi+xml})");
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

                        Session session = exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                                .getSession(exchange, exchange.getAttachment(SessionConfig.ATTACHMENT_KEY));

                        User user = (User) session.getAttribute(SessionHandler.USER);
                        String modelStr = RequestHelper.getStringFrom(exchange);

                        if (isJSON || isXMI) {
                            ModelLoader loader;
                            if (isJSON) {
                                loader = tempTransaction.createJSONLoader();
                            } else {
                                loader = tempTransaction.createXMILoader();
                            }

                            List<KMFContainer> models = loader.loadModelFromString(modelStr);
                            ModelCompare compare = tempTransaction.createModelCompare();
                            if (models != null) {
                                if (ModelHelper.canDeleteNamespace(user, models)) {
                                    for (KMFContainer model : models) {
                                        if (currentRoot != null) {
                                            tempTransaction.root(model);
                                            TraceSequence seq = compare.diff(currentRoot, model);
                                            TraceSequence delSeq = new TraceSequence(context.getKevoreeFactory());
                                            delSeq.setTraces(TraceHelper.delete(seq));
                                            delSeq.applyOn(currentRoot);
                                        }
                                    }
                                } else {
                                    exchange.setResponseCode(StatusCodes.FORBIDDEN);
                                    JsonObject response = new JsonObject();
                                    response.add("error", "You do not own the namespaces you want to delete");
                                    ResponseHelper.json(exchange, response);
                                    return;
                                }
                            } else {
                                exchange.setResponseCode(StatusCodes.BAD_REQUEST);
                                JsonObject response = new JsonObject();
                                response.add("error", "No model to load");
                                ResponseHelper.json(exchange, response);
                                return;
                            }
                        }

                        tempTransaction.close();
                        tempMemoryManager.close();
                        currentTransaction.commit();

                        exchange.setResponseCode(StatusCodes.OK);
                        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);

                    } catch (Exception e) {
                        log.error("500 - Internal Server Error - {}", exchange, e.getMessage());
                        log.debug("Caught exception", e);
                        e.printStackTrace();
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
            response.add("error", "Expect Content-Type to be set to application/{json, vnd.xmi+xml}");
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
        response.add("error", "Only accept application/json or text/html request");
        ResponseHelper.json(exchange, response);
    }
}
