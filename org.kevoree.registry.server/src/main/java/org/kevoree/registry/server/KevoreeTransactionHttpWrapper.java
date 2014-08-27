package org.kevoree.registry.server;

import io.undertow.io.UndertowInputStream;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import jet.runtime.typeinfo.JetValueParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kevoree.ContainerRoot;
import org.kevoree.factory.KevoreeTransaction;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.ModelPruner;
import org.kevoree.modeling.api.TransactionManager;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.modeling.api.util.ModelAttributeVisitor;
import org.kevoree.modeling.api.util.ModelVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by duke on 8/22/14.
 */
public class KevoreeTransactionHttpWrapper implements HttpHandler {

    private KevoreeTransactionManager manager;

    public KevoreeTransactionHttpWrapper(KevoreeTransactionManager ma) {
        this.manager = ma;
    }

    private ExecutorService dispatcher = Executors.newSingleThreadExecutor();

    @Override
    public void handleRequest(final HttpServerExchange httpServerExchange) throws Exception {
        if (httpServerExchange.getRequestPath().equals("/favicon.ico")) {
            httpServerExchange.getResponseSender().close();
        } else {
            if (httpServerExchange.getRequestMethod().equals(HttpString.tryFromString("GET"))) {
                KevoreeTransaction trans = manager.createTransaction();
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

                //TODO cache for request

                //pretty print
                if (httpServerExchange.getQueryParameters().get("json") != null) {
                    httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    if(selected.size()> 0){
                        //Create Pure Memory DataStore
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
                    httpServerExchange.endExchange();
                } else {
                    String basePath = httpServerExchange.getRelativePath();
                    if (basePath.equals("")) {
                        basePath = "/";
                    } else {
                        if (!basePath.endsWith("/")) {
                            basePath = basePath + "/";
                        }
                    }
                    String previousPath = basePath;
                    if (previousPath.length() > 2) {
                        String previous = basePath.substring(0, basePath.length() - 2);
                        previous = previous.substring(0, previous.lastIndexOf("/"));
                        previousPath = previous;
                    }
                    if (previousPath.equals("")) {
                        previousPath = ("/");
                    }
                    final String finalBasePath = basePath;
                    httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    final StringBuilder buffer = new StringBuilder();
                    buffer.append("<html><link rel=\"stylesheet\" href=\"//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css\"><body><div class=\"container\">");
                    buffer.append("<div class=\"panel panel-default\"><div class=\"panel-heading\">Children</div><div class=\"panel-body\">");
                    buffer.append("<ul class=\"list-group\">");
                    buffer.append("<li class=\"list-group-item\"><a href=\"");
                    buffer.append(previousPath);
                    buffer.append("\">parent</a></li>");
                    for (KMFContainer elem : selected) {
                        elem.visitContained(new ModelVisitor() {
                            @Override
                            public void visit(@JetValueParameter(name = "elem") @NotNull KMFContainer kmfContainer, @JetValueParameter(name = "refNameInParent") @NotNull String s, @JetValueParameter(name = "parent") @NotNull KMFContainer kmfContainer2) {
                                buffer.append("<li class=\"list-group-item\">");
                                String key = kmfContainer.getRefInParent() + "[" + kmfContainer.internalGetKey() + "]";
                                buffer.append("<a href=\"" + finalBasePath + kmfContainer.internalGetKey() + "\">");
                                buffer.append(key);
                                buffer.append("</a>");
                                buffer.append("</li>");
                            }
                        });
                    }
                    buffer.append("</ul>");
                    buffer.append("</div></div>");


                    for (KMFContainer elem : selected) {
                        buffer.append("<div class=\"panel panel-default\"><div class=\"panel-heading\">[" + elem.metaClassName() + "] " + elem.path() + "</div><div class=\"panel-body\">");
                        buffer.append("<ul class=\"list-group\">");
                        elem.visitAttributes(new ModelAttributeVisitor() {
                            @Override
                            public void visit(@Nullable @JetValueParameter(name = "value", type = "?") Object value, @NotNull @JetValueParameter(name = "name") String name, @NotNull @JetValueParameter(name = "parent") KMFContainer parent) {
                                buffer.append("<li class=\"list-group-item\">");
                                buffer.append(name);
                                buffer.append(":");
                                if (value != null) {
                                    buffer.append(value.toString());
                                }
                                buffer.append("</li>");
                            }
                        });
                        elem.visitNotContained(new ModelVisitor() {
                            @Override
                            public void visit(@NotNull @JetValueParameter(name = "elem") KMFContainer elem, @NotNull @JetValueParameter(name = "refNameInParent") String refNameInParent, @NotNull @JetValueParameter(name = "parent") KMFContainer parent) {
                                buffer.append("<li class=\"list-group-item\">");
                                buffer.append(refNameInParent);
                                buffer.append(":");
                                /*
                                buffer.append("<a href=\"");
                                buffer.append(elem.path());
                                buffer.append("\">");
                                */
                                buffer.append(elem.path());
                                //buffer.append("</a>");
                                buffer.append("</li>");
                            }
                        });
                        buffer.append("</ul>");
                        buffer.append("</div></div>");
                    }

                    buffer.append("</div></body></html>");
                    httpServerExchange.getResponseSender().send(buffer.toString());
                    httpServerExchange.endExchange();
                }
                trans.close();
            }
            if (httpServerExchange.getRequestMethod().equals(HttpString.tryFromString("POST"))) {
                if (httpServerExchange.isInIoThread()) {
                    httpServerExchange.dispatch(this);
                    return;
                }
                final String payloadRec = readBytesFromExchange(httpServerExchange);
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                httpServerExchange.getResponseSender().send("ack");
                httpServerExchange.endExchange();
                dispatcher.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            KevoreeTransaction currentTransaction = manager.createTransaction();
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
                            currentTransaction.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private static String readBytesFromExchange(HttpServerExchange exchange) throws IOException {
        InputStream inputStream = new UndertowInputStream(exchange);
        return getStringFromInputStream(inputStream);
    }

    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
