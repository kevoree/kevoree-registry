package org.kevoree.registry.server.util;

import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import java.util.List;
import java.util.Set;

/**
 *
 * Created by leiko on 08/12/14.
 */
public class ModelHelper {

    public static boolean canWriteNamespace(User user, List<KMFContainer> models) {
        int overall = 0;
        for (KMFContainer model : models) {
            overall += model.select("/packages[*]").size();
        }

        int count = 0;
        for (Namespace ns : user.getNamespaces()) {
            count += count(fqnToPath(ns.getFqn()), models);
        }

        return overall == count;
    }

    private static int count(String path, List<KMFContainer> models) {
        int count = 0;
        for (KMFContainer model : models) {
            count += model.select(path).size();
        }
        return count;
    }

    private static boolean hasOneThatStartsWith(String pkg, Set<Namespace> namespaces) {
        for (Namespace ns : namespaces) {
            if (ns.getFqn().startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    public static String fqnToPath(String namespace) {
        return "/packages[" + namespace.replaceAll("\\.", "]/packages[") + "]";
    }
}
