package org.kevoree.registry.server;

import io.undertow.io.UndertowInputStream;
import io.undertow.server.HttpServerExchange;
import jet.runtime.typeinfo.JetValueParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.util.ModelAttributeVisitor;
import org.kevoree.modeling.api.util.ModelVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by duke on 8/27/14.
 */
public class Helper {

    public static String generate(List<KMFContainer> selected, String relativePath) {
        String basePath = relativePath;
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

        if (selected.isEmpty()) {
            buffer.append("<li class=\"list-group-item list-group-item-warning\">");
            buffer.append("Unable to find <strong>");
            buffer.append(relativePath);
            buffer.append("</strong>");
            buffer.append("</li>");
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
        return buffer.toString();
    }

    public static String getStringFrom(HttpServerExchange exchange) {
        InputStream inputStream = new UndertowInputStream(exchange);
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
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
