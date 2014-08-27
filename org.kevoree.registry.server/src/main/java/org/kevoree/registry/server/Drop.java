package org.kevoree.registry.server;

import org.jetbrains.annotations.NotNull;
import org.kevoree.ContainerRoot;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.factory.KevoreeTransaction;
import org.kevoree.factory.KevoreeTransactionManager;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.ModelPruner;
import org.kevoree.modeling.api.Transaction;
import org.kevoree.modeling.api.TransactionManager;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;

import java.util.ArrayList;

/**
 * Created by duke on 8/27/14.
 */
public class Drop {

    public static void main(String[] args) {
        String path = "/packages[org]/packages[kevoree]/packages[library]/packages[defaultNodeTypes]/typeDefinitions[name=JavaNode,version=5.0.1-SNAPSHOT]";
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

        KMFContainer resolved = transaction.lookup(path);
        ArrayList<KMFContainer> resolveds = new ArrayList<KMFContainer>();
        resolveds.add(resolved);

        ModelPruner pruner = transaction.createModelPruner();
        TraceSequence prunedTraceSeq = pruner.prune(resolveds);

        System.err.println(prunedTraceSeq);

        MemoryDataStore tempStore = new MemoryDataStore();
        TransactionManager tempMemoryManager = new KevoreeTransactionManager(tempStore);
        KevoreeTransaction tempTransaction = (KevoreeTransaction) tempMemoryManager.createTransaction();
        ContainerRoot prunedRoot = tempTransaction.createContainerRoot();
        tempTransaction.root(prunedRoot);
        prunedTraceSeq.applyOn(prunedRoot);
        transaction.createJSONSerializer().serializeToStream(prunedRoot,System.out);
        tempTransaction.close();
        tempMemoryManager.close();


    }

}
