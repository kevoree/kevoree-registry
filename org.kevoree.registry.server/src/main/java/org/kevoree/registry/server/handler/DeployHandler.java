package org.kevoree.registry.server.handler;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import org.jetbrains.annotations.NotNull;
import org.kevoree.ContainerRoot;
import org.kevoree.DeployUnit;
import org.kevoree.TypeDefinition;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeTransaction;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.ModelLoader;
import org.kevoree.modeling.api.Transaction;
import org.kevoree.modeling.api.TransactionManager;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.trace.ModelRemoveTrace;
import org.kevoree.modeling.api.trace.ModelTrace;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.registry.server.util.ModelHelper;
import org.kevoree.registry.server.util.RequestHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duke on 8/27/14.
 */
public class DeployHandler implements HttpHandler {

    private KevoreeTransactionManager manager;

    public DeployHandler(KevoreeTransactionManager manager) {
        this.manager = manager;
    }

    @Override
    public void handleRequest(final HttpServerExchange httpServerExchange) throws Exception {
        final String payloadRec = RequestHelper.getStringFrom(httpServerExchange);
        String contentType = httpServerExchange.getRequestHeaders().get(Headers.CONTENT_TYPE).getFirst();
        final boolean isJSON = contentType.contains("application/json");
        final boolean isXMI = contentType.contains("application/vnd.xmi+xml");
        final boolean isTrace = contentType.contains("text/plain");

        if (!isJSON && !isXMI && !isTrace) {
            httpServerExchange.setResponseCode(406);
            httpServerExchange.getResponseSender().send("Unknown model mime type ("+contentType+")");
        }

        if (httpServerExchange.isInIoThread()) {
            httpServerExchange.dispatch(new Runnable() {
                @Override
                public void run() {
                	KevoreeTransaction currentTransaction = manager.createTransaction();
                	ContainerRoot regModel = (ContainerRoot) currentTransaction.lookup("/");
                	MemoryDataStore tempStore = new MemoryDataStore();
                    TransactionManager tempMemoryManager = new KevoreeTransactionManager(tempStore);
                    KevoreeTransaction tempTransaction = (KevoreeTransaction) tempMemoryManager.createTransaction();
                    TraceSequence regTraces = new TraceSequence(currentTransaction);
                    
                    try {
                        TraceSequence seq = null;

                        if (isJSON || isXMI) {
                            ModelLoader loader;
                            if (isJSON) {
                                loader = tempTransaction.createJSONLoader();
                            } else {
                                loader = tempTransaction.createXMILoader();
                            }
                            
                            KMFContainer model = loader.loadModelFromString(payloadRec).get(0);
                            
                            ModelCompare compare = currentTransaction.createModelCompare();
                            // for each typeDefinition in given model, try to find equivalent
                            // in current registry model in order to delete DeployUnit in conflict
                            List<KMFContainer> tdefs = model.select("**/typeDefinitions[]");
                            if (tdefs.size() > 0) {
                            	TypeDefinition tdef = (TypeDefinition) tdefs.get(0);
                            	TypeDefinition regTdef = (TypeDefinition) currentTransaction.lookup(tdef.path());
                            	if (regTdef != null) {
                            		// equivalent TypeDefinition found in registry
                            		// try to find equivalent DeployUnit in registry
                            		DeployUnit du = tdef.getDeployUnits().get(0);
                            		DeployUnit regDu = (DeployUnit) currentTransaction.lookup(du.path());
                        			if (regDu != null) {
                        				// equivalent DeployUnit already in registry
                        				// remove it in order to prevent conflict
                        				regTraces.append(compare.diff(regDu, du));
                        			}
                            	}	
                            }
                            
                            ContainerRoot newRootToCompare = tempTransaction.createContainerRoot().withGenerated_KMF_ID("0");
                            seq = compare.merge(newRootToCompare, model);

                        } else if (isTrace) {
                            seq = new TraceSequence(new DefaultKevoreeFactory(new MemoryDataStore()) {
                                @NotNull
                                @Override
                                public Transaction getOriginTransaction() {
                                    return null;
                                }
                            });
                            seq.populateFromString(payloadRec);
                        }
                        seq.append(regTraces);
                        seq.applyOn(regModel);
                    	
                        // update model id to the latest modification timestamp
                        regModel.setGenerated_KMF_ID(String.valueOf(System.currentTimeMillis()));

                        currentTransaction.commit();

                        httpServerExchange.setResponseCode(201);
                        httpServerExchange.getResponseSender().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        httpServerExchange.setResponseCode(500);
                        httpServerExchange.getResponseSender().send("Server error");
                    } finally {
                    	tempMemoryManager.close();
                    	tempTransaction.close();
                        currentTransaction.close();
                    }
                }
            });
        }
    }
}
