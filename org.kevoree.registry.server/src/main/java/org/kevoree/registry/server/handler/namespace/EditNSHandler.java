package org.kevoree.registry.server.handler.namespace;

import freemarker.template.SimpleHash;
import freemarker.template.Template;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.model.KevUser;
import org.kevoree.registry.server.model.Namespace;

import java.io.StringWriter;

/**
 * REST /!/ns/edit/:fqn
 * Created by leiko on 21/11/14.
 */
public class EditNSHandler extends AbstractNSHandler {



    @Override
    public void handleRequest(KevUser user, Namespace ns, HttpServerExchange exchange) throws Exception {
        SimpleHash root = new SimpleHash();
        root.put("readWrite", NamespaceDAO.getInstance().isOwner(ns, user));
        root.put("ns", ns);

//        Template tpl = config.getTemplate("profile.ftl");
//        StringWriter writer = new StringWriter();
//        tpl.process(root, writer);
//
//        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
//        exchange.getResponseSender().send(writer.toString());
//        exchange.getResponseSender().close();
//        exchange.endExchange();
    }
}
