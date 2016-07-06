package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.kevoree.registry.domain.*;
import org.kevoree.registry.repository.AuthorityRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.web.rest.dto.ErrorDTO;
import org.kevoree.registry.web.rest.dto.TypeDefinitionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
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
    private UserService userService;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private NamespaceRepository namespaceRepository;

    /**
     * GET  /tdefs -> returns all tdefs
     */
    @RequestMapping(value = "/tdefs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<List<TypeDefinition>> getTypeDefinitions() {
        log.debug("REST request to get all TypeDefinitions");
        return new ResponseEntity<>(tdefsRepository.findAll(), HttpStatus.OK);
    }

    /**
     * GET  /tdefs/:id : get the "id" TypeDefinition.
     *
     * @param id the id of the TypeDefinition to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the TypeDefinition, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/tdefs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeDefinition> getTypeDefinition(@PathVariable Long id) {
        log.debug("REST request to get TypeDefinition : {}", id);
        TypeDefinition tdef = tdefsRepository.findOne(id);
        return Optional.ofNullable(tdef)
            .map(du -> new ResponseEntity<>(du, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /namespaces/:namespace/tdefs -> get a list of typeDefinitions from a specific namespace
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<Set<TypeDefinition>> getTypeDefinitions(@PathVariable String namespace) {
        log.debug("REST request to get TypeDefinitions from Namespace: {}", namespace);
        return new ResponseEntity<>(tdefsRepository.findByNamespaceName(namespace), HttpStatus.OK);
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:name -> get a list of typeDefinitions from a specific namespace and name
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{name}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<Set<TypeDefinition>> getTypeDefinitionsByNamespaceAndName(@PathVariable String namespace, @PathVariable String name) {
        log.debug("REST request to get TypeDefinitions: {}.{}", namespace, name);
        return new ResponseEntity<>(tdefsRepository.findByNamespaceNameAndName(namespace, name), HttpStatus.OK);
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:name/:version -> get a precise type definition
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{name}/{version:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<TypeDefinition> getTypeDefinition(@PathVariable String namespace, @PathVariable String name, @PathVariable String version) {
        log.debug("REST request to get TypeDefinition: {}.{}/{}", namespace, name, version);
        return tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, name, version)
            .map(tdef -> new ResponseEntity<>(tdef, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST  /namespaces/:namespace/tdefs -> add type definition
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<?> addTypeDefinitions(@PathVariable String namespace, @Valid @RequestBody TypeDefinitionDTO tdefDTO) {
        log.debug("REST request to add a TypeDefinition: {} in Namespace: {}", tdefDTO, namespace);
        return userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
            .map(user ->
                Optional.ofNullable(namespaceRepository.findOne(namespace))
                    .map(foundNs ->
                        namespaceRepository.findOneByNameAndMemberName(namespace, user.getLogin())
                            .map(ns ->
                                tdefsRepository.findOneByNamespaceNameAndNameAndVersion(ns.getName(), tdefDTO.getName(), tdefDTO.getVersion())
                                    .map(tdef -> new ResponseEntity<>(
                                        new ErrorDTO(ns.getName()+"."+tdefDTO.getName()+"/"+tdefDTO.getVersion()+" already exists"),
                                        HttpStatus.BAD_REQUEST))
                                    .orElseGet(() -> {
                                        TypeDefinition tdef = new TypeDefinition();
                                        tdef.setName(tdefDTO.getName());
                                        tdef.setVersion(tdefDTO.getVersion());
                                        tdef.setModel(tdefDTO.getModel());
                                        tdef.setNamespace(ns);
                                        ns.addTypeDefinition(tdef);
                                        tdefsRepository.save(tdef);
                                        return new ResponseEntity<>(HttpStatus.CREATED);
                                    }))
                            .orElse(new ResponseEntity<>(new ErrorDTO("you are not a member of namespace '"+namespace+"'"), HttpStatus.FORBIDDEN)))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
            .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    /**
     * DELETE  /namespaces/{namespace}/tdefs/{name}/{version} -> delete the namespace.Name/Version TypeDefinition
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{name}/{version:.+}",
        method = RequestMethod.DELETE,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> delete(@PathVariable String namespace, @PathVariable String name, @PathVariable String version) {
        log.debug("REST request to delete TypeDefinition : {}.{}/{}", namespace, name, version);
        Namespace ns = namespaceRepository.findOne(namespace);
        if (ns == null) {
            return new ResponseEntity<>(new ErrorDTO("unable to find namespace " + name), HttpStatus.NOT_FOUND);
        } else {
            User user = userService.getUserWithAuthorities();
            Authority admin = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
            if (user.getAuthorities().contains(admin)) {
                tdefsRepository.delete(ns.getTypeDefinitions());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return namespaceRepository.findOneByNameAndMemberName(namespace, SecurityUtils.getCurrentLogin())
                    .map(ignore -> tdefsRepository.findOneByNamespaceNameAndNamespaceMembersLoginAndNameAndVersion(
                        namespace, SecurityUtils.getCurrentLogin(), name, version)
                        .map(tdef -> {
                            tdefsRepository.delete(tdef);
                            return new ResponseEntity<>(HttpStatus.OK);
                        })
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
                    .orElse(new ResponseEntity<>(new ErrorDTO("you are not a member of '" + namespace + "' namespace"), HttpStatus.FORBIDDEN));
            }
        }
    }

    /**
     * DELETE  /namespaces/{namespace}/tdefs/{name} -> delete all namespace.Name TypeDefinitions
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{name}",
        method = RequestMethod.DELETE,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> deleteAll(@PathVariable String namespace, @PathVariable String name) {
        log.debug("REST request to delete all TypeDefinitions : {}.{}", namespace, name);
        Namespace ns = namespaceRepository.findOne(namespace);
        if (ns == null) {
            return new ResponseEntity<>(new ErrorDTO("unable to find namespace "+name), HttpStatus.NOT_FOUND);
        } else {
            User user = userService.getUserWithAuthorities();
            Authority admin = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
            if (user.getAuthorities().contains(admin)) {
                tdefsRepository.delete(ns.getTypeDefinitions());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return namespaceRepository.findOneByNameAndMemberName(namespace, SecurityUtils.getCurrentLogin())
                    .map(ignore -> {
                        Set<TypeDefinition> tdefs = tdefsRepository
                            .findByNamespaceNameAndNamespaceMembersLoginAndName(namespace, SecurityUtils.getCurrentLogin(), name);
                        tdefsRepository.delete(tdefs);
                        return new ResponseEntity<>(HttpStatus.OK);
                    })
                    .orElse(new ResponseEntity<>(new ErrorDTO("you are not a member of '" + namespace + "' namespace"), HttpStatus.FORBIDDEN));
            }
        }
    }

    /**
     * DELETE /tdefs/:id -> delete "id" TypeDefinition
     */
    @RequestMapping(value = "/tdefs/{id}",
        method = RequestMethod.DELETE,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.debug("REST request to delete TypeDefinition: {}", id);
        return Optional.ofNullable(tdefsRepository.findOne(id))
            .map(tdef -> delete(tdef.getNamespace().getName(), tdef.getName(), tdef.getVersion()))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
