package org.kevoree.registry.domain.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.kevoree.registry.domain.TypeDefinition;

import java.io.IOException;

/**
 * Custom Jackson serializer for displaying Namespace objects.
 * Created by leiko on 08/01/15.
 */
public class CustomTypeDefinitionSerializer extends JsonSerializer<TypeDefinition> {

    @Override
    public void serialize(TypeDefinition tdef, JsonGenerator generator, SerializerProvider serializer)
        throws IOException {
        generator.writeStartObject();
        if (tdef.getName() != null) {
            generator.writeStringField("name", tdef.getName());
        }
        if (tdef.getVersion() != null) {
            generator.writeStringField("version", tdef.getVersion());
        }
        if (tdef.getNamespace() != null) {
            generator.writeObjectFieldStart("namespace");
            generator.writeStringField("name", tdef.getNamespace().getName());
            generator.writeStringField("owner", tdef.getNamespace().getOwner().getLogin());
            generator.writeEndObject();
        }
        if (tdef.getSerializedModel() != null) {
            generator.writeStringField("serializedModel", tdef.getSerializedModel());
        }
        if (tdef.getNbDownloads() != null) {
            generator.writeNumberField("nbDownloads", tdef.getNbDownloads());
        }
        generator.writeEndObject();
    }
}
