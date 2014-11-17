package org.kevoree.registry.server;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import io.undertow.Undertow;
import org.jetbrains.annotations.NotNull;
import org.kevoree.ContainerRoot;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.factory.KevoreeTransaction;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.modeling.api.Transaction;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;
import org.kevoree.registry.server.handler.MainHandler;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by duke on 8/22/14.
 */
public class App {
    public static void main(String[] args) throws IOException {
        KevoreeFactory factory = new DefaultKevoreeFactory(new MemoryDataStore()) {
            @NotNull
            @Override
            public Transaction getOriginTransaction() {
                return null;
            }
        };
        String kevoreeVersion = factory.getVersion();
        if (kevoreeVersion.contains(".")) {
            kevoreeVersion = kevoreeVersion.substring(0, kevoreeVersion.indexOf("."));
        }
        System.out.println("Kevoree Registry Server (v"+kevoreeVersion+")");
        final LevelDbDataStore dataStore = new LevelDbDataStore("kev_db_" + kevoreeVersion);
        System.out.println("Database location: "+ Paths.get(dataStore.getDbStorageBasePath()).toAbsolutePath());
        final KevoreeTransactionManager manager = new KevoreeTransactionManager(dataStore);
        KevoreeTransaction transaction = manager.createTransaction();
        ContainerRoot root = (ContainerRoot) transaction.lookup("/");
        if (root == null) {
            root = transaction.createContainerRoot().withGenerated_KMF_ID("0");
            transaction.root(root);
        }
        transaction.commit();
        transaction.close();

        String port = "8080";
        if (System.getProperty("port") != null) {
            port = System.getProperty("port");
        }

        String host = "0.0.0.0";
        if (System.getProperty("host") != null) {
            host = System.getProperty("host");
        }

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(App.class, "/WEB-INF/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        Undertow server = Undertow.builder()
                .addHttpListener(Integer.parseInt(port), host)
                .setHandler(new MainHandler(manager, cfg, factory.getVersion()))
                .build();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                manager.close();
            }
        });
        server.start();
        System.out.println("Listening on "+host+":"+port);
    }

}
