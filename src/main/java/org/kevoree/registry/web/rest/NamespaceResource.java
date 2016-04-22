package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.web.rest.util.HeaderUtil;
import org.kevoree.registry.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
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
    
    /**
     * POST  /namespaces : Create a new namespace.
     *
     * @param namespace the namespace to create
     * @return the ResponseEntity with status 201 (Created) and with body the new namespace, or with status 400 (Bad Request) if the namespace has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/namespaces",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Namespace> createNamespace(@Valid @RequestBody Namespace namespace) throws URISyntaxException {
        log.debug("REST request to save Namespace : {}", namespace);
        if (namespace.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("namespace", "idexists", "A new namespace cannot already have an ID")).body(null);
        }
        Namespace result = namespaceRepository.save(namespace);
        return ResponseEntity.created(new URI("/api/namespaces/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("namespace", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /namespaces : Updates an existing namespace.
     *
     * @param namespace the namespace to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated namespace,
     * or with status 400 (Bad Request) if the namespace is not valid,
     * or with status 500 (Internal Server Error) if the namespace couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/namespaces",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Namespace> updateNamespace(@Valid @RequestBody Namespace namespace) throws URISyntaxException {
        log.debug("REST request to update Namespace : {}", namespace);
        if (namespace.getId() == null) {
            return createNamespace(namespace);
        }
        Namespace result = namespaceRepository.save(namespace);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("namespace", namespace.getId().toString()))
            .body(result);
    }

    /**
     * GET  /namespaces : get all the namespaces.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of namespaces in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/namespaces",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Namespace>> getAllNamespaces(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Namespaces");
        Page<Namespace> page = namespaceRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/namespaces");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /namespaces/:id : get the "id" namespace.
     *
     * @param id the id of the namespace to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the namespace, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/namespaces/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Namespace> getNamespace(@PathVariable Long id) {
        log.debug("REST request to get Namespace : {}", id);
        Namespace namespace = namespaceRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(namespace)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /namespaces/:id : delete the "id" namespace.
     *
     * @param id the id of the namespace to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/namespaces/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteNamespace(@PathVariable Long id) {
        log.debug("REST request to delete Namespace : {}", id);
        namespaceRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("namespace", id.toString())).build();
    }

}