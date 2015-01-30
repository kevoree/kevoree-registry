package org.kevoree.registry.domain.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;

import java.io.IOException;

/**
 * Custom Jackson serializer for displaying Namespace objects.
 * Created by leiko on 08/01/15.
 */
public class CustomNamespaceSerializer extends JsonSerializer<Namespace> {

    @Override
    public void serialize(Namespace namespace, JsonGenerator generator, SerializerProvider serializer)
        throws IOException {
        generator.writeStartObject();
        if (namespace.getName() != null) {
            generator.writeStringField("name", namespace.getName());
        }

        if (namespace.getOwner() != null) {
            generator.writeStringField("owner", namespace.getOwner().getLogin());
        } else {
            generator.writeNullField("owner");
        }

        generator.writeArrayFieldStart("members");
        for (User u : namespace.getMembers()) {
            generator.writeString(u.getLogin());
        }
        generator.writeEndArray();
        generator.writeEndObject();
    }
}
