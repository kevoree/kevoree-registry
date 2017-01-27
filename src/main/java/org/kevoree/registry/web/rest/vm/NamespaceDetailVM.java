package org.kevoree.registry.web.rest.vm;

import org.kevoree.registry.service.dto.NamespaceDTO;

import java.util.Set;

/**
 *
 * Created by leiko on 1/26/17.
 */
public class NamespaceDetailVM extends NamespaceDTO {

    private Set<String> typeDefinitions;
}
