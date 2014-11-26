package org.kevoree.registry.server.handler.model;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import org.kevoree.registry.server.handler.AbstractTemplateHandler;
import org.kevoree.registry.server.template.TemplateManager;

import java.util.Deque;
import java.util.Map;

/**
 * Created by leiko on 25/11/14.
 */
public class SearchModelHandler extends AbstractTemplateHandler {

    public SearchModelHandler(TemplateManager tplManager) {
        super(tplManager);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Map<String, Deque<String>> params = exchange.getQueryParameters();

        // TODO this could use some better parsing, cause "as is" it can explode in soooo many cases
        StringBuilder modelQuery = new StringBuilder();
        if (params.get("q") != null) {
            String query = params.get("q").getFirst();
            if (!query.trim().isEmpty()) {
                String[] split = query.split("/");
                String[] parts = split[0].split("\\.");
                String name = parts[parts.length - 1];
                if (Character.isUpperCase(name.charAt(0))) {
                    // query is for a DeployUnit or a TypeDefinition
                    // first of all, append packages..
                    if (parts.length == 1) {
                        // query does not specify package (so it is a Kevoree Std Library TypeDefinition or DeployUnit)
                        modelQuery.append("/org/kevoree/library");
                    } else {
                        // prepend with packages
                        for (int i=0; i < parts.length - 1; i++) {
                            modelQuery.append("/");
                            modelQuery.append(parts[i]);
                        }
                    }

                    // now append TypeDef or DeployUnit name
                    modelQuery.append("/name=");
                    modelQuery.append(name);
                } else {
                    // query is for a package
                    for (String pkg : parts) {
                        modelQuery.append("/");
                        modelQuery.append(pkg);
                    }
                }

                if (split.length > 1) {
                    // a version is specified
                    modelQuery.append(",version=");
                    modelQuery.append(split[1]);
                }
            } else {
                modelQuery.append("/");
            }
        } else {
            modelQuery.append("/");
        }

        new RedirectHandler(modelQuery.toString()).handleRequest(exchange);
    }
}
