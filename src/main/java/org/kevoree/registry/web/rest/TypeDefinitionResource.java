package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
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
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.web.rest.dto.ErrorDTO;
import org.kevoree.registry.web.rest.dto.TypeDefinitionDTO;
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
    @GetMapping("/tdefs")
    @Timed
    List<TypeDefinition> getTypeDefinitions() {
        log.debug("REST request to get all TypeDefinitions");
        return tdefsRepository.findAll();
    }

//    /**
//     * GET  /tdefs -> returns all latest tdefs
//     */
//    @RequestMapping(value = "/tdefs/latest",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    List<TypeDefinition> getLatestTypeDefinitions() {
//        log.debug("REST request to get all latest TypeDefinitions");
//        List<TypeDefinition> latestTdefs = new ArrayList<>();
//        for (TypeDefinition tdef : tdefsRepository.findAllDistinctName()) {
//            Optional<TypeDefinition> latestTdef = tdefsRepository.findFirst1ByNamespaceNameAndNameOrderByVersionDesc(
//                    tdef.getNamespace().getName(), tdef.getName());
//            if (latestTdef.isPresent()) {
//                latestTdefs.add(latestTdef.get());
//            }
//        }
//
//        return latestTdefs;
//    }

    /**
     * GET  /tdefs -> returns all tdefs
     */
    @GetMapping("/tdefs/page")
    @Timed
    public Page<TypeDefinition> getPageableTypeDefinitions(Pageable pageable) {
        log.debug("REST request to get paged TypeDefinitions (index={}, size={})", pageable.getPageNumber(), pageable.getPageSize());
        return tdefsRepository.findAll(pageable);
    }

    /**
     * GET  /tdefs/:id : get the "id" TypeDefinition.
     *
     * @param id the id of the TypeDefinition to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the TypeDefinition, or with status 404 (Not Found)
     */
    @GetMapping("/tdefs/{id:[\\d]+}")
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
    @GetMapping("/namespaces/{namespace}/tdefs")
    @Timed
    ResponseEntity<Set<TypeDefinition>> getTypeDefinitions(@PathVariable String namespace) {
        log.debug("REST request to get TypeDefinitions from Namespace: {}", namespace);
        return new ResponseEntity<>(tdefsRepository.findByNamespaceName(namespace), HttpStatus.OK);
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:name -> get a list of typeDefinitions from a specific namespace and name
     */
    @GetMapping("/namespaces/{namespace}/tdefs/{name}")
    @Timed
    ResponseEntity<Set<TypeDefinition>> getTypeDefinitionsByNamespaceAndName(@PathVariable String namespace, @PathVariable String name) {
        log.debug("REST request to get TypeDefinitions: {}.{}", namespace, name);
        return new ResponseEntity<>(tdefsRepository.findByNamespaceNameAndName(namespace, name), HttpStatus.OK);
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:name/:version -> get a precise type definition
     */
    @GetMapping("/namespaces/{namespace}/tdefs/{name}/{version}")
    @Timed
    ResponseEntity<TypeDefinition> getTypeDefinition(@PathVariable String namespace, @PathVariable String name, @PathVariable Long version) {
        log.debug("REST request to get TypeDefinition: {}.{}/{}", namespace, name, version);
        return tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, name, version)
            .map(tdef -> new ResponseEntity<>(tdef, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST  /namespaces/:namespace/tdefs -> add type definition
     */
    @PostMapping("/namespaces/{namespace}/tdefs")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    ResponseEntity<?> addTypeDefinition(@PathVariable String namespace, @Valid @RequestBody TypeDefinitionDTO tdefDTO) {
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
    @DeleteMapping("/namespaces/{namespace}/tdefs/{name}/{version}")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<?> delete(@PathVariable String namespace, @PathVariable String name, @PathVariable Long version) {
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
    @DeleteMapping("/namespaces/{namespace}/tdefs/{name}")
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

    /**
     * GET /namespaces/:namespace/tdef/:name/latest
     *
     * @param namespace the name of the namespace you want to find TypeDefinition from
     * @param name the name of the latest typeDefinition
     * @return the ResponseEntity with status 200 (OK) and with body the typeDefinition, or with status 404 (Not Found)
     */
    @GetMapping("/namespaces/{namespace}/tdef/{name}/latest")
    public ResponseEntity<TypeDefinitionDTO> getLatestTypeDefinition(@PathVariable String namespace, @PathVariable String name) {
        log.debug("REST request to get the latest TypeDefinition for {}.{}", namespace, name);
        Optional<TypeDefinition> tdef = tdefsRepository.findFirst1ByNamespaceNameAndNameOrderByVersionDesc(namespace, name);
        if (!tdef.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new TypeDefinitionDTO(tdef.get()), HttpStatus.OK);
        }
    }
}
