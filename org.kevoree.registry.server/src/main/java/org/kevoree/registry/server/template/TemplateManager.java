package org.kevoree.registry.server.template;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.IOException;
import java.io.StringWriter;

/**
 *
 * Created by leiko on 25/11/14.
 */
public class TemplateManager {

    private Configuration freemarker;
    private Template layout;
    private SimpleHash layoutData;

    public TemplateManager(Configuration config, String layout) throws IOException {
        this.freemarker = config;
        this.layout = this.freemarker.getTemplate(layout);
        this.layoutData = new SimpleHash();
    }

    public void template(HttpServerExchange exchange, String template)
            throws Exception {
        this.template(exchange, new SimpleHash(), template);
    }

    public void template(HttpServerExchange exchange, SimpleHash data, String template)
            throws Exception {
        Template content = this.freemarker.getTemplate(template);
        StringWriter writer = new StringWriter();

        if (data == null) { data = new SimpleHash(); }
        // inherit from layout data in sub template
        data.putAll(this.layoutData.toMap());
        // create an string from template & data
        content.process(data, writer);

        StringWriter layoutWriter = new StringWriter();
        this.layoutData.put("content", writer.toString());
        this.layout.process(this.layoutData, layoutWriter);

        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/html");
        exchange.getResponseSender().send(layoutWriter.toString());
        exchange.getResponseSender().close(IoCallback.END_EXCHANGE);
    }

    public void putLayoutData(String key, Object value) {
        this.layoutData.put(key, value);
    }
}
