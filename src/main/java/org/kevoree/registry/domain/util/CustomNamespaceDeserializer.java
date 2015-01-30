package org.kevoree.registry.domain.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;

import java.io.IOException;
import java.util.Iterator;

/**
 * Custom Jackson deserializer for displaying Namespace objects.
 */
public class CustomNamespaceDeserializer extends JsonDeserializer<Namespace> {

    @Override
    public Namespace deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        Namespace ns = new Namespace();
        if (node.get("name") != null) {
            ns.setName(node.get("name").asText());
        }

        if (node.get("owner") != null) {
            String ownerLogin = node.get("owner").asText();
            User owner = new User();
            owner.setLogin(ownerLogin);
            ns.setOwner(owner);
        }

        if (node.get("members") != null) {
            Iterator<JsonNode> members = node.get("members").elements();
            while (members.hasNext()) {
                User member = new User();
                member.setLogin(members.next().asText());
                ns.addMember(member);
            }
        }

        return ns;
    }
}
