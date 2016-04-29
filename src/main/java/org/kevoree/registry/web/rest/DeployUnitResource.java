package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.service.DeployUnitService;
import org.kevoree.registry.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing DeployUnit.
 */
@RestController
@RequestMapping("/api")
public class DeployUnitResource {

    private final Logger log = LoggerFactory.getLogger(DeployUnitResource.class);
        
    @Inject
    private DeployUnitService deployUnitService;
    
    /**
     * POST  /deploy-units : Create a new deployUnit.
     *
     * @param deployUnit the deployUnit to create
     * @return the ResponseEntity with status 201 (Created) and with body the new deployUnit, or with status 400 (Bad Request) if the deployUnit has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/deploy-units",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeployUnit> createDeployUnit(@Valid @RequestBody DeployUnit deployUnit) throws URISyntaxException {
        log.debug("REST request to save DeployUnit : {}", deployUnit);
        if (deployUnit.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("deployUnit", "idexists", "A new deployUnit cannot already have an ID")).body(null);
        }
        DeployUnit result = deployUnitService.save(deployUnit);
        return ResponseEntity.created(new URI("/api/deploy-units/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("deployUnit", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /deploy-units : Updates an existing deployUnit.
     *
     * @param deployUnit the deployUnit to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated deployUnit,
     * or with status 400 (Bad Request) if the deployUnit is not valid,
     * or with status 500 (Internal Server Error) if the deployUnit couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/deploy-units",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeployUnit> updateDeployUnit(@Valid @RequestBody DeployUnit deployUnit) throws URISyntaxException {
        log.debug("REST request to update DeployUnit : {}", deployUnit);
        if (deployUnit.getId() == null) {
            return createDeployUnit(deployUnit);
        }
        DeployUnit result = deployUnitService.save(deployUnit);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("deployUnit", deployUnit.getId().toString()))
            .body(result);
    }

    /**
     * GET  /deploy-units : get all the deployUnits.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of deployUnits in body
     */
    @RequestMapping(value = "/deploy-units",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<DeployUnit> getAllDeployUnits() {
        log.debug("REST request to get all DeployUnits");
        return deployUnitService.findAll();
    }

    /**
     * GET  /deploy-units/:id : get the "id" deployUnit.
     *
     * @param id the id of the deployUnit to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnit, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/deploy-units/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeployUnit> getDeployUnit(@PathVariable Long id) {
        log.debug("REST request to get DeployUnit : {}", id);
        DeployUnit deployUnit = deployUnitService.findOne(id);
        return Optional.ofNullable(deployUnit)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /deploy-units/:id : delete the "id" deployUnit.
     *
     * @param id the id of the deployUnit to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/deploy-units/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDeployUnit(@PathVariable Long id) {
        log.debug("REST request to delete DeployUnit : {}", id);
        deployUnitService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("deployUnit", id.toString())).build();
    }

}
