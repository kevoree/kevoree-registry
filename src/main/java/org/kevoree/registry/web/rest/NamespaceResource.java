package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.json.JSONException;
import org.json.JSONObject;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.service.NamespaceService;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.web.rest.dto.search.NamespaceSearchDTO;
import org.kevoree.registry.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing Namespace.
 */
@RestController
@RequestMapping("/api")
public class NamespaceResource {

    private final Logger log = LoggerFactory.getLogger(NamespaceResource.class);

    @Inject
    private NamespaceService namespaceService;

    @Inject
    private UserService userService;

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

        final Optional<Namespace> newNamespace = namespaceService.save(namespace);

        ResponseEntity<Namespace> ret;
        if (newNamespace.isPresent()) {
            final Namespace result = newNamespace.get();
            ret = ResponseEntity.created(new URI("/api/namespaces/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("namespace", result.getId().toString()))
                .body(result);
        } else {
            ret = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ret;


    }

    /**
     * GET  /namespaces : get all the namespaces.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of namespaces in body
     */
    @RequestMapping(value = "/namespaces",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Namespace> getAllNamespaces() {
        log.debug("REST request to get all Namespaces");
        return namespaceService.findAll();
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
        Namespace namespace = namespaceService.findOne(id);
        return Optional.ofNullable(namespace)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/namespaces/{id}/deactivate",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Namespace> deactivateNamespace(@PathVariable Long id) {
        log.debug("REST request to deactive Namespace : {}", id);
        final Namespace namespace = namespaceService.findOne(id);
        return Optional.ofNullable(namespace)
            .map((result) -> {
                final User currentUser = userService.getUserWithAuthorities();
                final ResponseEntity<Namespace> ret;
                if (Objects.equals(currentUser.getId(), result.getOwner().getId())) {
                    final Namespace res2 = namespaceService.deactivate(result);
                    ret = new ResponseEntity<>(res2, HttpStatus.OK);
                } else {
                    ret = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
                return ret;
            }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/namespaces/{id}/members",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addMembers(@PathVariable("id") final Long namespaceId, @RequestBody final Set<Long> membersId) {
        final Namespace namespace = namespaceService.findOne(namespaceId);
        return Optional.ofNullable(namespace).map(result -> {
            final User currentUser = userService.getUserWithAuthorities();
            final ResponseEntity ret;
            if (Objects.equals(currentUser.getId(), result.getOwner().getId())) {
                final List<User> userEntities = userService.findByIds(membersId);
                if (userEntities.size() == membersId.size()) {
                    result.getMembers().addAll(userEntities);
                    final Namespace res = this.namespaceService.update(result);
                    ret = new ResponseEntity<>(res, HttpStatus.OK);
                } else {
                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.append("error", "Some members does not exists");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ret = new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
                }
            } else {
                ret = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return ret;
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/namespaces/{id}/members",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity removeMembers(@PathVariable("id") final Long namespaceId, @RequestBody final Set<Long> membersId) {
        final Namespace namespace = namespaceService.findOne(namespaceId);
        return Optional.ofNullable(namespace).map(result -> {
            final User currentUser = userService.getUserWithAuthorities();
            final ResponseEntity ret;
            if (Objects.equals(currentUser.getId(), result.getOwner().getId())) {
                final List<User> userEntities = userService.findByIds(membersId);
                if (userEntities.size() == membersId.size()) {
                    final Set<User> remainingMembers = result.getMembers().stream().filter(m -> !membersId.contains(m.getId())).collect(Collectors.toSet());
                    result.setMembers(remainingMembers);
                    final Namespace res = this.namespaceService.update(result);
                    ret = new ResponseEntity<>(res, HttpStatus.OK);
                } else {
                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.append("error", "Some members does not exists");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ret = new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
                }
            } else {
                ret = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return ret;
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/namespaces/search",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Namespace>> searchNamespaces(@Valid @RequestBody NamespaceSearchDTO namespaceSearch) {
        log.debug("REST request to search namespaces by name");
        return new ResponseEntity<>(namespaceService.search(namespaceSearch), HttpStatus.OK);
    }
}
