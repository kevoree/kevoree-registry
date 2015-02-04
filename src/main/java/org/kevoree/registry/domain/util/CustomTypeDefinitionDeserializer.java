package org.kevoree.registry.domain.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;

import java.io.IOException;
import java.util.Iterator;

/**
 * Custom Jackson deserializer for displaying Namespace objects.
 */
public class CustomTypeDefinitionDeserializer extends JsonDeserializer<TypeDefinition> {

    @Override
    public TypeDefinition deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        TypeDefinition tdef = new TypeDefinition();
        if (node.get("name") != null) {
            tdef.setName(node.get("name").asText());
        }

        if (node.get("version") != null) {
            tdef.setVersion(node.get("version").asText());
        }

        if (node.get("namespace") != null) {
            Namespace ns = new Namespace();
            ns.setName(node.get("namespace").get("name").asText());
            User owner = new User();
            owner.setLogin(node.get("namespace").get("owner").asText());
            ns.setOwner(owner);
            tdef.setNamespace(ns);
        }

        if (node.get("nbDownloads") != null) {
            tdef.setNbDownloads(node.get("nbDownloads").asLong());
        }

        if (node.get("serializedModel") != null) {
            tdef.setSerializedModel(node.get("serializedModel").asText());
        }

        return new TypeDefinition();
    }
}
