package org.kevoree.registry.server;

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

/**
 * Created by duke on 8/22/14.
 */
public class App {

    public static void main(String[] args) {
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
        System.out.println("Kevoree Registry Server...., majorVersion=" + kevoreeVersion);
        final LevelDbDataStore dataStore = new LevelDbDataStore("kev_db_" + kevoreeVersion);
        final KevoreeTransactionManager manager = new KevoreeTransactionManager(dataStore);
        KevoreeTransaction transaction = manager.createTransaction();
        ContainerRoot root = (ContainerRoot) transaction.lookup("/");
        if (root == null) {
            root = transaction.createContainerRoot().withGenerated_KMF_ID("0");
            transaction.root(root);
        }
        transaction.commit();
        transaction.close();
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new KevoreeTransactionHttpWrapper(manager)).build();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                manager.close();
                dataStore.sync();
            }
        });
        server.start();
    }

}
