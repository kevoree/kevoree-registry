package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.exception.InvalidNamespaceException;
import org.kevoree.registry.exception.InvalidUserException;
import org.kevoree.registry.exception.NotTheOwnerException;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.service.NamespaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Namespace.
 */
@RestController
@RequestMapping("/api")
public class NamespaceResource {

    private final Logger log = LoggerFactory.getLogger(NamespaceResource.class);

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private NamespaceService namespaceService;

    /**
     * POST  /rest/namespaces -> create a new namespace.
     */
    @RequestMapping(value = "/namespaces",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody Namespace namespace) {
        log.debug("REST request to save Namespace : {}", namespace);
        try {
            namespaceService.createNamespace(namespace.getFqn());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (InvalidNamespaceException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST  /rest/namespace/:fqn/leave -> leave a namespace
     */
    @RequestMapping(value = "/namespaces/{fqn:.+}/leave",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> leave(@PathVariable String fqn) {
        log.debug("REST request to leave Namespace : {}", fqn);
        try {
            namespaceService.leaveNamespace(fqn);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidNamespaceException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InvalidUserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * POST  /rest/namespaces/:fqn/add_member -> add a member to a namespace
     */
    @RequestMapping(value = "/namespaces/{fqn:.+}/add_member",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> addMember(@PathVariable String fqn, @RequestBody String userLogin) {
        log.debug("REST request to add '{}' to namespace '{}'", userLogin, fqn);
        try {
            namespaceService.addMember(fqn, userLogin);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidNamespaceException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InvalidUserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotTheOwnerException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * POST  /rest/namespaces/:fqn/remove_member -> remove a member from a namespace
     */
    @RequestMapping(value = "/namespaces/{fqn:.+}/remove_member",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> removeMember(@PathVariable String fqn, @RequestBody String userLogin) {
        log.debug("REST request to remove '{}' from namespace '{}'", userLogin, fqn);
        try {
            namespaceService.removeMember(fqn, userLogin);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidNamespaceException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InvalidUserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotTheOwnerException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * GET  /rest/namespaces -> get all the namespaces.
     */
    @RequestMapping(value = "/namespaces",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Namespace> getAll() {
        log.debug("REST request to get all Namespaces");
        return namespaceRepository.findAll();
    }

    /**
     * GET  /rest/namespaces/:fqn -> get the "fqn" namespace.
     */
    @RequestMapping(value = "/namespaces/{fqn:.+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Namespace> get(@PathVariable String fqn) {
        log.debug("REST request to get Namespace : {}", fqn);
        return Optional.ofNullable(namespaceRepository.findOne(fqn))
            .map(namespace -> new ResponseEntity<>(
                namespace,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/namespaces/:fqn -> delete the "fqn" namespace.
     */
    @RequestMapping(value = "/namespaces/{fqn:.+}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable String fqn) {
        log.debug("REST request to delete Namespace : {}", fqn);
        namespaceRepository.delete(fqn);
    }
}
