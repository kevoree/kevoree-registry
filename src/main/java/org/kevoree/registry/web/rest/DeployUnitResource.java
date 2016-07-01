package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.repository.DeployUnitRepository;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.kevoree.registry.service.DeployUnitService;
import org.kevoree.registry.web.rest.dto.DeployUnitDTO;
import org.kevoree.registry.web.rest.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Path;
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
    private DeployUnitRepository duRepository;

    @Inject
    private TypeDefinitionRepository tdefsRepository;

    @Inject
    private DeployUnitService duService;

    /**
     * POST  /dus : Create a new deployUnit.
     *
     * @param deployUnit the deployUnit to create
     * @return the ResponseEntity with status 201 (Created) and with body the new deployUnit, or with status 400 (Bad Request) if the deployUnit has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/dus",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createDeployUnit(@Valid @RequestBody DeployUnitDTO deployUnit) throws URISyntaxException {
        log.debug("REST request to save DeployUnit : {}", deployUnit);
        if (deployUnit.getTdefId() != null) {
            TypeDefinition tdef = tdefsRepository.findOne(deployUnit.getTdefId());
            if (tdef != null) {
                if (deployUnit.getId() != null) {
                    return new ResponseEntity<>(new ErrorDTO("A new DeployUnit cannot already have an ID"), HttpStatus.BAD_REQUEST);
                } else {
                    if (duService.canCreate(deployUnit.getTdefId())) {
                        DeployUnit savedDu = duService.create(deployUnit);
                        return new ResponseEntity<>(savedDu, HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                }
            } else {
                return new ResponseEntity<>(
                    new ErrorDTO("Unknown TypeDefinition " + deployUnit.getTdefId()),
                    HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(
                new ErrorDTO("The given DeployUnit did not specify its TypeDefinition ID"),
                HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT  /dus : Updates an existing deployUnit.
     *
     * @param deployUnit the deployUnit to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated deployUnit,
     * or with status 400 (Bad Request) if the deployUnit is not valid,
     * or with status 500 (Internal Server Error) if the deployUnit couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/dus",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeployUnit> updateDeployUnit(@Valid @RequestBody DeployUnit deployUnit) throws URISyntaxException {
        log.debug("REST request to update DeployUnit : {}", deployUnit);
//        if (deployUnit.getId() == null) {
//            return createDeployUnit(deployUnit);
//        }
//        DeployUnit result = deployUnitRepository.save(deployUnit);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert("deployUnit", deployUnit.getId().toString()))
//            .body(result);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET  /dus : get all the deployUnits.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of deployUnits in body
     */
    @RequestMapping(value = "/dus",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<DeployUnit> getAllDeployUnits() {
        log.debug("REST request to get all DeployUnits");
        return duRepository.findAll();
    }

    /**
     * GET  /dus/:id : get the "id" deployUnit.
     *
     * @param id the id of the deployUnit to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnit, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/dus/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeployUnit> getDeployUnit(@PathVariable Long id) {
        log.debug("REST request to get DeployUnit : {}", id);
        DeployUnit deployUnit = duRepository.findOne(id);
        return Optional.ofNullable(deployUnit)
            .map(du -> new ResponseEntity<>(du, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /dus/:namespace/:tdefName/:tdefVersion/:name/:version/:platform : get the a specific DeployUnit
     *
     * @param namespace   namespace name
     * @param tdef        typeDefinition name
     * @param tdefVersion typeDefinition version
     * @param name        deployUnit name
     * @param version     deployUnit version
     * @param platform    deployUnit platform
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnit, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/dus/{namespace}/{tdef}/{tdefVersion}/{name}/{version:.+}/{platform}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getSpecificDeployUnit(
        @PathVariable String namespace, @PathVariable String tdef, @PathVariable String tdefVersion,
        @PathVariable String name, @PathVariable String version, @PathVariable String platform) {
        log.debug("REST request to get DeployUnit : {}.{}/{} {}-{}-{}", namespace, tdef, tdefVersion, name, version, platform);
        return tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, tdef, tdefVersion)
            .map(typeDef ->
                duRepository.findOneByTypeDefinitionIdAndNameAndVersionAndPlatform(typeDef.getId(), name, version, platform)
                    .map(du -> new ResponseEntity<>(du, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /dus/:id : delete the "id" deployUnit.
     *
     * @param id the id of the deployUnit to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/dus/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDeployUnit(@PathVariable Long id) {
        log.debug("REST request to delete DeployUnit : {}", id);
//        deployUnitRepository.delete(id);
//        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("deployUnit", id.toString())).build();
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
