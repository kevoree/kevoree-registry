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
//        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get();
//        Set<TypeDefinition> tdefs = new HashSet<>();
//        for (Namespace ns : user.getNamespaces()) {
//            tdefs.addAll(ns.getTypeDefinitions());
//        }
//        return new ResponseEntity<>(tdefs, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST  /tdefs -> add type definitions
     */
    @RequestMapping(value = "/tdefs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<?> addTypeDefinitions(@RequestBody TypeDefinition tdef) {
        log.debug("REST request to add a TypeDefinition: {}", tdef);
//        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get();
//        Namespace ns = namespaceRepository.findOneByMemberName(user.getLogin()).get();
//        ns.addTypeDefinition(tdef);
//        tdefsRepository.save(tdef);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
