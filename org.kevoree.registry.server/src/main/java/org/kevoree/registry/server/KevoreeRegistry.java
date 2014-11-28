package org.kevoree.registry.server;

import io.undertow.Undertow;
import org.jetbrains.annotations.NotNull;
import org.kevoree.ContainerRoot;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.factory.KevoreeTransaction;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.modeling.api.Transaction;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;
import org.kevoree.registry.server.handler.KevoreeRegistryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Kevoree Registry main
 * Created by duke on 8/22/14.
 */
public class KevoreeRegistry {

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final Logger log = LoggerFactory.getLogger(KevoreeRegistry.class.getSimpleName());

        // create model datastore
        final LevelDbDataStore dataStore = new LevelDbDataStore("kevoree-registry-db");
        log.info("Database location: {}", Paths.get(dataStore.getDbStorageBasePath()).toAbsolutePath());

        // create Kevoree factory
        KevoreeFactory factory = new DefaultKevoreeFactory(dataStore) {
            @NotNull
            @Override
            public Transaction getOriginTransaction() {
                return null;
            }
        };
        log.info("Kevoree Registry Server (v{})", factory.getVersion());

        // create Kevoree transaction manager
        final KevoreeTransactionManager manager = new KevoreeTransactionManager(dataStore);

        KevoreeTransaction transaction = manager.createTransaction();
        ContainerRoot root = (ContainerRoot) transaction.lookup("/");
        if (root == null) {
            root = transaction.createContainerRoot().withGenerated_KMF_ID("0");
            transaction.root(root);
        }
        transaction.commit();
        transaction.close();

        String port = System.getProperty("port", "8080");
        String host = System.getProperty("host", "0.0.0.0");

        Undertow server = Undertow.builder()
                .addHttpListener(Integer.parseInt(port), host)
                .setHandler(new KevoreeRegistryHandler(manager, factory))
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Shutting down data store...");
                manager.close();
                log.info("Database gracefully closed.");
            }
        });
        server.start();
        log.info("Listening on {}:{}", host, port);
    }
}
