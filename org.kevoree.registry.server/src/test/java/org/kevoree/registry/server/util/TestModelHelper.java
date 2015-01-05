package org.kevoree.registry.server.util;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.ModelLoader;
import org.kevoree.modeling.api.Transaction;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
* Created by leiko on 17/12/14.
*/
public class TestModelHelper {

    private KevoreeFactory factory;

    @Before
    public void before() {
        // create Kevoree factory
        this.factory = new DefaultKevoreeFactory(new MemoryDataStore()) {
            @NotNull
            @Override
            public Transaction getOriginTransaction() {
                return null;
            }
        };
    }

    @Test
    public void testCanWriteNamespace() {
        ModelLoader loader = factory.createJSONLoader();
        List<KMFContainer> models = loader.loadModelFromStream(TestModelHelper.class.getResourceAsStream("/JavascriptNode.json"));
        for (KMFContainer model : models) {
            factory.root(model);
        }

        User user = new User();
        Namespace ns0 = new Namespace();
        Namespace ns1 = new Namespace();

        ns0.setFqn("fr.braindead");
        ns1.setFqn("org.kevoree.library");
        user.addNamespace(ns0);
        user.addNamespace(ns1);

        User user2 = new User();
        Namespace ns2 = new Namespace();

        ns2.setFqn("org.kevoree.baz");
        user2.addNamespace(ns0);
        user2.addNamespace(ns2);

        assertTrue(ModelHelper.canWriteNamespace(user, models));
        assertFalse(ModelHelper.canWriteNamespace(user2, models));
        assertFalse(ModelHelper.canWriteNamespace(new User(), models));
    }
}
