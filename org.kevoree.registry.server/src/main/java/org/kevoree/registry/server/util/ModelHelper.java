package org.kevoree.registry.server.util;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.util.ModelAttributeVisitor;
import org.kevoree.modeling.api.util.ModelVisitor;

import java.util.List;

/**
 * Created by duke on 8/27/14.
 */
public class ModelHelper {

    public static String generatePreviousPath(String relativePath) {
        if (relativePath.equals("")) {
            relativePath = "/";
        } else {
            if (!relativePath.endsWith("/")) {
                relativePath = relativePath + "/";
            }
        }
        String previousPath = relativePath;
        if (previousPath.length() > 2) {
            String previous = relativePath.substring(0, relativePath.length() - 2);
            previous = previous.substring(0, previous.lastIndexOf("/"));
            previousPath = previous;
        }
        if (previousPath.equals("")) {
            previousPath = ("/");
        }

        return previousPath;
    }

    public static SimpleSequence generateChildren(List<KMFContainer> selected, final String relativePath) {
        String basePath = relativePath;
        if (basePath.equals("")) {
            basePath = "/";
        } else {
            if (!basePath.endsWith("/")) {
                basePath = basePath + "/";
            }
        }
        final String finalBasePath = basePath;
        final SimpleSequence elements = new SimpleSequence();

        for (KMFContainer elem : selected) {
            elem.visitContained(new ModelVisitor() {
                @Override
                public void visit(@NotNull KMFContainer kmfContainer, @NotNull String s, @NotNull KMFContainer kmfContainer2) {
                    SimpleHash elemData = new SimpleHash();
                    String key = kmfContainer.getRefInParent() + "[" + kmfContainer.internalGetKey() + "]";
                    elemData.put("key", key);
                    elemData.put("link", finalBasePath + kmfContainer.internalGetKey());
                    elements.add(elemData);
                }
            });
        }

        return elements;

    }

    public static SimpleSequence generateElements(List<KMFContainer> selected) {
        final SimpleSequence elements = new SimpleSequence();

        for (final KMFContainer elem : selected) {
            final SimpleHash elemData = new SimpleHash();
            elements.add(elemData);
            elemData.put("name", "["+elem.metaClassName()+"] "+elem.path());
            final SimpleSequence attributes = new SimpleSequence();
            elemData.put("attributes", attributes);

            elem.visitAttributes(new ModelAttributeVisitor() {
                @Override
                public void visit(@Nullable Object value, @NotNull String name, @NotNull KMFContainer parent) {
                    if (value == null) { value = ""; }
                    attributes.add(name + ":" + value.toString());
                }
            });

            elem.visitNotContained(new ModelVisitor() {
                @Override
                public void visit(@NotNull KMFContainer elem, @NotNull String refNameInParent, @NotNull KMFContainer parent) {
                    attributes.add(refNameInParent + ":" + elem.path());
                }
            });
        }

        return elements;
    }
}