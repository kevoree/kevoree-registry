package org.kevoree.registry.server.util;

import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by leiko on 08/12/14.
 */
public class ModelHelper {

    public static boolean canDeleteNamespace(User user, List<KMFContainer> models) {
        Set<Integer> levels = new HashSet<Integer>();
        for (Namespace ns : user.getNamespaces()) {
            levels.add(ns.getFqn().split("\\.").length);
        }

        if (levels.isEmpty()) {
            return false;
        }

        for (Integer level : levels) {
            if (!canDeleteNamespace(user, models, level)) {
                return false;
            }
        }

        return true;
    }

    public static boolean canWriteNamespace(User user, List<KMFContainer> models) {
        Set<Integer> levels = new HashSet<Integer>();
        for (Namespace ns : user.getNamespaces()) {
            levels.add(ns.getFqn().split("\\.").length);
        }

        if (levels.isEmpty()) {
            return false;
        }

        for (Integer level : levels) {
            if (!canWriteNamespace(user, models, level)) {
                return false;
            }
        }

        return true;
    }

    public static String fqnToPath(String namespace) {
        return "packages[" + namespace.replaceAll("\\.", "]/packages[") + "]";
    }

    private static boolean canWriteNamespace(User user, List<KMFContainer> models, int level) {
        String path = "";
        for (int i=0; i < level; i++) {
            if (path.isEmpty()) {
                path += "packages[*]";
            } else {
                path += "/packages[*]";
            }
        }

        int overall = 0;
        for (KMFContainer model : models) {
            overall += model.select(path).size();
        }

        int count = 0;
        for (Namespace ns : user.getNamespaces()) {
            count += count(fqnToPath(ns.getFqn()), models);
        }

        return overall == count;
    }

    private static boolean canDeleteNamespace(User user, List<KMFContainer> models, int level) {
        String path = "";
        for (int i=0; i < level; i++) {
            if (path.isEmpty()) {
                path += "packages[*]";
            } else {
                path += "/packages[*]";
            }
        }

        int overall = 0;
        for (KMFContainer model : models) {
            overall += model.select(path).size();
        }

        int count = 0;
        for (Namespace ns : user.getNamespaces()) {
            if (ns.getOwner().getId().equals(user.getId())) {
                count += count(fqnToPath(ns.getFqn()), models);
            }
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
}
