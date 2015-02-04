package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.web.rest.dto.TypeDefinitionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * REST controller for managing models.
 */
@RestController
@RequestMapping("/api")
public class TypeDefinitionResource {

    private final Logger log = LoggerFactory.getLogger(TypeDefinitionResource.class);

    @Inject
    private TypeDefinitionRepository tdefsRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private NamespaceRepository namespaceRepository;

    /**
     * GET  /tdefs -> get currently logged in user type definitions
     */
    @RequestMapping(value = "/tdefs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<Set<TypeDefinition>> getTypeDefinitions() {
        log.debug("REST request to get type definitions for user: {}", SecurityUtils.getCurrentLogin());
        return userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
            .map(user -> {
                Set<TypeDefinition> tdefs = new HashSet<>();
                user.getNamespaces().forEach(
                    n -> tdefs.addAll(n.getTypeDefinitions()));
                return new ResponseEntity<>(tdefs, HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    /**
     * GET  /tdefs/:namespace/:name/:version -> get a precise type definition
     */
    @RequestMapping(value = "/tdefs/{namespace}/{name}/{version:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<TypeDefinition> getNamespace(@PathVariable String namespace, @PathVariable String name, @PathVariable String version) {
        log.debug("REST request to get TypeDefinition: {}.{}/{}", namespace, name, version);
        return tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, name, version)
            .map(tdef -> new ResponseEntity<>(tdef, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST  /tdefs -> add type definitions
     */
    @RequestMapping(value = "/tdefs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<?> addTypeDefinitions(@RequestBody TypeDefinitionDTO tdefDTO) {
        log.debug("REST request to add a TypeDefinition: {}", tdefDTO);
        return userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
            .map(user -> namespaceRepository.findOneByNameAndMemberName(tdefDTO.getNamespace(), user.getLogin())
                .map(ns -> tdefsRepository.findOneByNamespaceNameAndNameAndVersion(ns.getName(), tdefDTO.getName(), tdefDTO.getVersion())
                    .map(tdef -> new ResponseEntity<>("typeDefinition already exists", HttpStatus.BAD_REQUEST))
                    .orElseGet(() -> {
                        TypeDefinition tdef = new TypeDefinition();
                        tdef.setName(tdefDTO.getName());
                        tdef.setVersion(tdefDTO.getVersion());
                        tdef.setSerializedModel(tdefDTO.getSerializedModel());
                        tdef.setNamespace(ns);
                        ns.addTypeDefinition(tdef);
                        tdefsRepository.save(tdef);
                        return new ResponseEntity<>(HttpStatus.CREATED);
                    }))
                .orElse(new ResponseEntity<>("unknown namespace", HttpStatus.BAD_REQUEST)))
            .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }
}
