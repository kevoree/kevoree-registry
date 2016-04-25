package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.service.TypeDefinitionService;
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
 * REST controller for managing TypeDefinition.
 */
@RestController
@RequestMapping("/api")
public class TypeDefinitionResource {

    private final Logger log = LoggerFactory.getLogger(TypeDefinitionResource.class);
        
    @Inject
    private TypeDefinitionService typeDefinitionService;
    
    /**
     * POST  /type-definitions : Create a new typeDefinition.
     *
     * @param typeDefinition the typeDefinition to create
     * @return the ResponseEntity with status 201 (Created) and with body the new typeDefinition, or with status 400 (Bad Request) if the typeDefinition has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/type-definitions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeDefinition> createTypeDefinition(@Valid @RequestBody TypeDefinition typeDefinition) throws URISyntaxException {
        log.debug("REST request to save TypeDefinition : {}", typeDefinition);
        if (typeDefinition.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("typeDefinition", "idexists", "A new typeDefinition cannot already have an ID")).body(null);
        }
        TypeDefinition result = typeDefinitionService.save(typeDefinition);
        return ResponseEntity.created(new URI("/api/type-definitions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("typeDefinition", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /type-definitions : Updates an existing typeDefinition.
     *
     * @param typeDefinition the typeDefinition to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated typeDefinition,
     * or with status 400 (Bad Request) if the typeDefinition is not valid,
     * or with status 500 (Internal Server Error) if the typeDefinition couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/type-definitions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeDefinition> updateTypeDefinition(@Valid @RequestBody TypeDefinition typeDefinition) throws URISyntaxException {
        log.debug("REST request to update TypeDefinition : {}", typeDefinition);
        if (typeDefinition.getId() == null) {
            return createTypeDefinition(typeDefinition);
        }
        TypeDefinition result = typeDefinitionService.save(typeDefinition);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("typeDefinition", typeDefinition.getId().toString()))
            .body(result);
    }

    /**
     * GET  /type-definitions : get all the typeDefinitions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of typeDefinitions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/type-definitions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TypeDefinition>> getAllTypeDefinitions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of TypeDefinitions");
        Page<TypeDefinition> page = typeDefinitionService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/type-definitions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /type-definitions/:id : get the "id" typeDefinition.
     *
     * @param id the id of the typeDefinition to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the typeDefinition, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/type-definitions/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeDefinition> getTypeDefinition(@PathVariable Long id) {
        log.debug("REST request to get TypeDefinition : {}", id);
        TypeDefinition typeDefinition = typeDefinitionService.findOne(id);
        return Optional.ofNullable(typeDefinition)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /type-definitions/:id : delete the "id" typeDefinition.
     *
     * @param id the id of the typeDefinition to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/type-definitions/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTypeDefinition(@PathVariable Long id) {
        log.debug("REST request to delete TypeDefinition : {}", id);
        typeDefinitionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("typeDefinition", id.toString())).build();
    }

}