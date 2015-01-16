package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.KevoreeDimension;
import org.KevoreeUniverse;
import org.KevoreeView;
import org.kevoree.ContainerRoot;
import org.kevoree.Group;
import org.kevoree.modeling.api.Callback;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

/**
 * REST controller for managing models.
 */
@RestController
@RequestMapping("/api")
public class ModelResource {

    private final Logger log = LoggerFactory.getLogger(ModelResource.class);

    /**
     * GET  /model?p={path} -> get the "path" entity.
     */
    @RequestMapping(value = "/model",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<ContainerRoot> getModel(@RequestParam(required = false) String p) {
        log.debug("REST request to get model: {}", p);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * POST  /model -> get the models from the paths specified in the body
     */
    @RequestMapping(value = "/model",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<?> getModels(@RequestBody List<String> paths) {
        log.debug("REST request to get models: {}", paths);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST  /model/merge -> merge given model
     */
    @RequestMapping(value = "/model/merge",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<?> mergeModel(@RequestBody String modelStr) {
        log.debug("REST request to merge model: {}", modelStr.substring(0, 20));
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE  /model/{path} -> delete given path
     */
    @RequestMapping(value = "/model/{path:.+}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<?> deleteModel(@PathVariable String path) {
        log.debug("REST request to delete model: {}", path);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
