package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.kevoree.registry.config.Constants;
import org.kevoree.registry.domain.Authority;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.AuthorityRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.service.TypeDefinitionService;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.service.dto.ErrorDTO;
import org.kevoree.registry.service.dto.TypeDefinitionDTO;
import org.kevoree.registry.service.mapper.TypeDefinitionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

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
    private TypeDefinitionService tdefsService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private TypeDefinitionMapper tdefMapper;

    /**
     * GET  /tdefs -> returns all tdefs
     */
    @GetMapping("/tdefs")
    @Timed
    public Set<TypeDefinitionDTO> getTypeDefinitions() {
        log.debug("REST request to get all TypeDefinitions");
        return tdefsService.getAll();
    }

    /**
     * GET  /tdefs -> returns all tdefs
     */
    @GetMapping("/tdefs/page")
    @Timed
    public Page<TypeDefinitionDTO> getPageableTypeDefinitions(Pageable pageable,
                                                              @RequestParam(required = false, defaultValue = "false") boolean latest) {
        log.debug("REST request to get paged TypeDefinitions (index={}, size={}, onlyLatest={})",
                pageable.getPageNumber(), pageable.getPageSize(), latest);
        return tdefsService.getPage(pageable, latest);
    }

    /**
     * GET  /tdefs/:id : get the "id" TypeDefinition.
     *
     * @param id the id of the TypeDefinition to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the TypeDefinition, or with status 404 (Not Found)
     */
    @GetMapping("/tdefs/{id:[\\d]+}")
    @Timed
    public ResponseEntity<TypeDefinitionDTO> getTypeDefinition(@PathVariable Long id) {
        log.debug("REST request to get TypeDefinition by id={}", id);
        return Optional.of(tdefsService.findOne(id))
                .map(tdef -> new ResponseEntity<>(tdefMapper.typeDefinitionToTypeDefinitionDTO(tdef), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * GET  /namespaces/:namespace/tdefs -> get the typeDefinitions of a specific namespace
     */
    @GetMapping("/namespaces/{namespace:" + Constants.NS_NAME_REGEX + "}/tdefs")
    @Timed
    public ResponseEntity<Set<TypeDefinitionDTO>> getNamespaceTdefs(@PathVariable String namespace,
                                                                    @RequestParam(required = false) String version) {
        log.debug("REST request to get namespace tdefs: {}", namespace);
        Set<TypeDefinitionDTO> tdefs = tdefsService.getAllByNamespace(namespace);
        if (version != null && version.equals("latest")) {
            return new ResponseEntity<>(tdefsService.onlyLatest(tdefs.stream()).collect(Collectors.toSet()), HttpStatus.OK);
        }
        return new ResponseEntity<>(tdefs, HttpStatus.OK);
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:name -> get a list of typeDefinitions from a specific namespace and name
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{name:"+ Constants.TDEF_NAME_REGEX+"}")
    @Timed
    ResponseEntity<Set<TypeDefinitionDTO>> getTypeDefinitionsByNamespaceAndName(@PathVariable String namespace,
                                                                             @PathVariable String name) {
        log.debug("REST request to get TypeDefinitions: {}.{}", namespace, name);
        return new ResponseEntity<>(tdefsService.getAllByNamespaceAndName(namespace, name), HttpStatus.OK);
    }

    /**
     * GET /namespaces/:namespace/tdef/:name/latest -> the latest tdef of a specfic namespace and name
     *
     * @param namespace the name of the namespace you want to find TypeDefinition from
     * @param name the name of the latest typeDefinition
     * @return the ResponseEntity with status 200 (OK) and with body the typeDefinition, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{name:"+ Constants.TDEF_NAME_REGEX+"}/latest")
    public ResponseEntity<TypeDefinitionDTO> getLatestTypeDefinition(@PathVariable String namespace,
                                                                     @PathVariable String name) {
        log.debug("REST request to get the latest TypeDefinition for {}.{}", namespace, name);
        return tdefsService.findLatestByNamespaceAndName(namespace, name)
                .map(tdef -> new ResponseEntity<>(new TypeDefinitionDTO(tdef), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:name/:version -> get a precise type definition
     */
    @GetMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{name:"+ Constants.TDEF_NAME_REGEX+"}/{version:[\\d]+}")
    @Timed
    public ResponseEntity<TypeDefinitionDTO> getTypeDefinition(@PathVariable String namespace, @PathVariable String name,
                                                        @PathVariable Long version) {
        log.debug("REST request to get TypeDefinition: {}.{}/{}", namespace, name, version);
        return tdefsService.findByNamespaceAndNameAndVersion(namespace, name, version)
            .map(tdef -> new ResponseEntity<>(tdefMapper.typeDefinitionToTypeDefinitionDTO(tdef), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST  /namespaces/:namespace/tdefs -> add type definition
     */
    @PostMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<?> addTypeDefinition(@PathVariable String namespace, @Valid @RequestBody TypeDefinitionDTO tdefDTO) {
        log.debug("REST request to add a TypeDefinition: {} in Namespace: {}", tdefDTO, namespace);
        return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin())
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
                                        tdef.setCreatedBy(SecurityUtils.getCurrentUserLogin());
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
    @DeleteMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{name:"+ Constants.TDEF_NAME_REGEX+"}/{version:[\\d]+}")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<?> delete(@PathVariable String namespace, @PathVariable String name,
                                    @PathVariable Long version) {
        log.debug("REST request to delete TypeDefinition : {}.{}/{}", namespace, name, version);
        Namespace ns = namespaceRepository.findOne(namespace);
        if (ns == null) {
            return new ResponseEntity<>(new ErrorDTO("unable to find namespace " + name), HttpStatus.NOT_FOUND);
        } else {
            User user = userService.getUserWithAuthorities();
            if (user != null) {
                Authority admin = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
                if (user.getAuthorities().contains(admin)) {
                    return tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, name, version)
                        .map(tdef -> {
                            tdefsRepository.delete(tdef);
                            return new ResponseEntity<>(HttpStatus.OK);
                        })
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                } else {
                    return namespaceRepository.findOneByNameAndMemberName(namespace, SecurityUtils.getCurrentUserLogin())
                        .map(ignore -> tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, name, version)
                            .map(tdef -> {
                                tdefsRepository.delete(tdef);
                                return new ResponseEntity<>(HttpStatus.OK);
                            })
                            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
                        .orElse(new ResponseEntity<>(new ErrorDTO("you are not a member of '" + namespace + "' namespace"), HttpStatus.FORBIDDEN));
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
    }

    /**
     * DELETE  /namespaces/{namespace}/tdefs/{name} -> delete all namespace.Name TypeDefinitions
     */
    @DeleteMapping("/namespaces/{namespace:"+ Constants.NS_NAME_REGEX+"}/tdefs/{name:"+ Constants.TDEF_NAME_REGEX+"}")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<?> deleteAll(@PathVariable String namespace, @PathVariable String name) {
        log.debug("REST request to delete all TypeDefinitions : {}.{}", namespace, name);
        Namespace ns = namespaceRepository.findOne(namespace);
        if (ns == null) {
            return new ResponseEntity<>(new ErrorDTO("unable to find namespace "+name), HttpStatus.NOT_FOUND);
        } else {
            User user = userService.getUserWithAuthorities();
            if (user != null) {
                Authority admin = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
                if (user.getAuthorities().contains(admin)) {
                    tdefsRepository.delete(tdefsRepository.findByNamespaceNameAndName(namespace, name));
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return namespaceRepository.findOneByNameAndMemberName(namespace, SecurityUtils.getCurrentUserLogin())
                        .map(ignore -> {
                            tdefsRepository.delete(tdefsRepository.findByNamespaceNameAndName(namespace, name));
                            return new ResponseEntity<>(HttpStatus.OK);
                        })
                        .orElse(new ResponseEntity<>(new ErrorDTO("you are not a member of '" + namespace + "' namespace"), HttpStatus.FORBIDDEN));
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
    }

    /**
     * DELETE /tdefs/:id -> delete a specifid TypeDefinition
     */
    @DeleteMapping("/tdefs/{id:[\\d]+}")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        log.debug("REST request to delete TypeDefinition with id: {}", id);
        TypeDefinition tdef = tdefsRepository.findOne(id);
        return this.delete(tdef.getNamespace().getName(), tdef.getName(), tdef.getVersion());
    }
}
