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
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
     * POST  /namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus : Create a new deployUnit.
     *
     * @param namespace the namespace name to find the typeDefinition in
     * @param tdefName the typeDefinition name to attach the deployUnit to
     * @param tdefVersion the typeDefinition version to attach de deployUnit to
     * @param deployUnit the deployUnit to create
     * @return the ResponseEntity with status 201 (Created) and with body the new deployUnit, or with status 400 (Bad Request) if the deployUnit has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createDeployUnit(@PathVariable String namespace, @PathVariable String tdefName,
                                              @PathVariable String tdefVersion,
                                              @Valid @RequestBody DeployUnitDTO deployUnit) throws URISyntaxException {
        log.debug("REST request to create DeployUnit: {} for {}.{}/{}", deployUnit, namespace, tdefName, tdefVersion);
        if (deployUnit.getId() == null) {
            return tdefsRepository.findOneByNamespaceNameAndNameAndVersion(namespace, tdefName, tdefVersion)
                .map(tdef -> {
                    if (duService.canCreate(tdef.getId())) {
                        DeployUnit savedDu = duService.create(tdef, deployUnit);
                        return new ResponseEntity<>(savedDu, HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                })
                .orElse(new ResponseEntity<>(
                    new ErrorDTO("Unknown TypeDefinition " + namespace + "." + tdefName + "/" + tdefVersion),
                    HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(new ErrorDTO("A new DeployUnit cannot already have an ID"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT  /namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus : Updates a deployUnit.
     *
     * @param namespace the namespace name to find the typeDefinition in
     * @param tdefName the typeDefinition name to attach the deployUnit to
     * @param tdefVersion the typeDefinition version to attach de deployUnit to
     * @param deployUnit the deployUnit to create
     * @return the ResponseEntity with status 200 (OK) and with body the updated deployUnit,
     * or with status 400 (Bad Request) if the deployUnit is not valid,
     * or with status 500 (Internal Server Error) if the deployUnit couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> updateDeployUnit(@PathVariable String namespace, @PathVariable String tdefName,
                                              @PathVariable String tdefVersion,
                                              @Valid @RequestBody DeployUnitDTO deployUnit) throws URISyntaxException {
        log.debug("REST request to update DeployUnit: {} in {}.{}/{}", namespace, tdefName, tdefVersion, deployUnit.getId());
        if (deployUnit.getId() == null) {
            return createDeployUnit(namespace, tdefName, tdefVersion, deployUnit);
        }
        DeployUnit du = duRepository.findOne(deployUnit.getId());
        du.setName(deployUnit.getName());
        du.setVersion(deployUnit.getVersion());
        du.setPlatform(deployUnit.getPlatform());
        du.setModel(deployUnit.getModel());
        DeployUnit result = duRepository.save(du);
        return new ResponseEntity<>(result, HttpStatus.OK);
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
     * PUT  /dus/:id : update the "id" deployUnit.
     *
     * @param id the id of the deployUnit to update
     * @param deployUnit the deployUnit to create
     * @return the ResponseEntity with status 200 (OK) and with body the updated deployUnit,
     * or with status 400 (Bad Request) if the deployUnit is not valid,
     * or with status 500 (Internal Server Error) if the deployUnit couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/dus/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DeployUnit> updateDeployUnit(@PathVariable Long id,
                                                       @Valid @RequestBody DeployUnitDTO deployUnit) throws URISyntaxException {
//        log.debug("REST request to update DeployUnit : {}", id);
//        if (deployUnit.getId() == null) {
//            createDeployUnit()
//        } else {
//
//        }
//        DeployUnit du = duService.update(deployUnit);
//        return Optional.ofNullable(deployUnit)
//            .map(du -> new ResponseEntity<>(du, HttpStatus.OK))
//            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET  /namespaces/:namespace/dus : get the all the deployUnits attached to a specified namespace.
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/namespaces/{namespace}/dus",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Set<DeployUnit> getDeployUnit(@PathVariable String namespace) {
        log.debug("REST request to get DeployUnits from Namespace : {}", namespace);
        return duRepository.findByNamespace(namespace);
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/dus : get the all the deployUnits attached to a specified namespace and a specified
     * typeDefinition name
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{tdefName}/dus",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Set<DeployUnit> getDeployUnit(@PathVariable String namespace, @PathVariable String tdefName) {
        log.debug("REST request to get DeployUnits from Namespace: {} and TypeDefinition: {}", namespace, tdefName);
        return duRepository.findByNamespaceAndTypeDefinition(namespace, tdefName);
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus : get the all the deployUnits attached to a
     * specified namespace and a specified typeDefinition name and version
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Set<DeployUnit> getDeployUnit(@PathVariable String namespace, @PathVariable String tdefName,
                                         @PathVariable String tdefVersion) {
        log.debug("REST request to get DeployUnits from Namespace: {} and TypeDefinition: {}/{}",
            namespace, tdefName, tdefVersion);
        return duRepository.findByNamespaceAndTypeDefinitionAndTypeDefinitionVersion(namespace, tdefName, tdefVersion);
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus/:name : get the all the deployUnits with a specific
     * name and attached to a specified namespace and a specified typeDefinition name and version
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @param name the name of the DeployUnits
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Set<DeployUnit> getDeployUnit(@PathVariable String namespace, @PathVariable String tdefName,
                                         @PathVariable String tdefVersion, @PathVariable String name) {
        log.debug("REST request to get DeployUnits {} from Namespace: {} and TypeDefinition: {}/{}", name,
            namespace, tdefName, tdefVersion);
        return duRepository.findByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndName(
            namespace, tdefName, tdefVersion, name);
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus/:name/:version : get the all the deployUnits with
     * a specific name and version attached to a specified namespace and a specified typeDefinition name and version
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @param name the name of the DeployUnits
     * @param version the version of the DeployUnits
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}/{version}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Set<DeployUnit> getDeployUnits(@PathVariable String namespace, @PathVariable String tdefName,
                                         @PathVariable String tdefVersion, @PathVariable String name,
                                         @PathVariable String version) {
        log.debug("REST request to get DeployUnits {}-{} from Namespace: {} and TypeDefinition: {}/{}", name, version,
            namespace, tdefName, tdefVersion);
        return duRepository.findByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndNameAndVersion(
            namespace, tdefName, tdefVersion, name, version);
    }

    /**
     * GET  /namespaces/:namespace/tdefs/:tdefName/:tdefVersion/dus/:name/:version/:platform : get a specific deployUnit
     * version
     *
     * @param namespace the name of the namespace you want to list deployUnits from
     * @param tdefName the name of the typeDefinition
     * @param tdefVersion the version of the typeDefinition
     * @param name the name of the DeployUnits
     * @param version the version of the DeployUnits
     * @param platform the platform of the DeployUnits
     * @return the ResponseEntity with status 200 (OK) and with body the deployUnits, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/namespaces/{namespace}/tdefs/{tdefName}/{tdefVersion}/dus/{name}/{version}/{platform}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public DeployUnit getDeployUnit(@PathVariable String namespace, @PathVariable String tdefName,
                                         @PathVariable String tdefVersion, @PathVariable String name,
                                         @PathVariable String version, @PathVariable String platform) {
        log.debug("REST request to get DeployUnits {}-{}-{} from Namespace: {} and TypeDefinition: {}/{}", name,
            version, platform, namespace, tdefName, tdefVersion);
        return duRepository.findOneByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndNameAndVersionAndPlatform(
            namespace, tdefName, tdefVersion, name, version, platform);
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
        duRepository.delete(id);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
