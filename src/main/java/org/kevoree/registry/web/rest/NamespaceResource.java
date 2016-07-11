package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.kevoree.registry.domain.Authority;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.AuthorityRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.web.rest.dto.ErrorDTO;
import org.kevoree.registry.web.rest.dto.NamedDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing models.
 */
@RestController
@RequestMapping("/api")
public class NamespaceResource {

    private final Logger log = LoggerFactory.getLogger(NamespaceResource.class);

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private AuthorityRepository authorityRepository;

    /**
     * GET  /namespaces -> get all namespaces
     */
    @RequestMapping(value = "/namespaces",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<List<Namespace>> getNamespaces() {
        log.debug("REST request to get namespaces for user: {}", SecurityUtils.getCurrentLogin());
        return new ResponseEntity<>(namespaceRepository.findAll(), HttpStatus.OK);
    }

    /**
     * GET  /namespaces/:name -> get a precise namespace
     */
    @RequestMapping(value = "/namespaces/{name:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<Namespace> getNamespace(@PathVariable String name) {
        log.debug("REST request to get namespace: {}", name);
        return Optional.ofNullable(namespaceRepository.findOne(name))
            .map(namespace -> new ResponseEntity<>(namespace,HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST  /namespaces -> add a namespace
     */
    @RequestMapping(value = "/namespaces",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<?> addNamespaces(@Valid @RequestBody NamedDTO namedDTO) {
        log.debug("REST request to add a namespace: {}", namedDTO);
        return Optional.ofNullable(namespaceRepository.findOne(namedDTO.getName()))
            .map(ns -> new ResponseEntity<>(new ErrorDTO("name already in use"), HttpStatus.BAD_REQUEST))
            .orElseGet(() -> userRepository.findOneByLogin(namedDTO.getName())
                .map(user -> new ResponseEntity<>(new ErrorDTO("name already in use"), HttpStatus.BAD_REQUEST))
                .orElseGet(() -> userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
                    .map(user -> {
                        Namespace newNs = new Namespace();
                        newNs.setName(namedDTO.getName());
                        newNs.setOwner(user);
                        newNs.addMember(user);
                        user.addNamespace(newNs);
                        namespaceRepository.save(newNs);
                        userRepository.save(user);
                        return new ResponseEntity(HttpStatus.CREATED);
                    })
                    .orElse(new ResponseEntity(HttpStatus.FORBIDDEN))));
    }

    /**
     * POST  /namespace/{name}/members -> add a member to a namespace
     */
    @RequestMapping(value = "/namespaces/{name}/members",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<?> addMemberToNamespace(@PathVariable String name, @Valid @RequestBody NamedDTO namedDTO) {
        log.debug("REST request to add '{}' to namespace '{}'", namedDTO, name);
        final Namespace ns = namespaceRepository.findOne(name);
        if (ns == null) {
            return new ResponseEntity<>(new ErrorDTO("unknown namespace"), HttpStatus.NOT_FOUND);
        } else {
            // retrieve currently logged-in user
            return userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
                .map(user -> {
                    // check that the current logged-in user owns the namespace
                    if (ns.getOwner().getLogin().equals(user.getLogin())) {
                        return userRepository.findOneByLogin(namedDTO.getName())
                            // retrieve user to add as a member from the db
                            .map(member -> namespaceRepository.findOneByNameAndMemberName(ns.getName(), member.getLogin())
                                // this user is already a member of the given namespace
                                .map(n -> new ResponseEntity<>(new ErrorDTO("already a member of the namespace"), HttpStatus.BAD_REQUEST))
                                .orElseGet(() -> {
                                    // add member to namespace
                                    ns.addMember(member);
                                    member.addNamespace(ns);
                                    namespaceRepository.save(ns);
                                    userRepository.save(member);
                                    return new ResponseEntity<>(HttpStatus.OK);
                                }))
                                // cannot add unknown user to a namespace
                            .orElse(new ResponseEntity<>(new ErrorDTO("unknown user"), HttpStatus.BAD_REQUEST));
                    } else {
                        return new ResponseEntity<>(new ErrorDTO("you are not the owner of the namespace"), HttpStatus.UNAUTHORIZED);
                    }
                })
                    // user must be logged-in
                .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
        }
    }

    /**
     * DELETE  /namespace/{name}/members/{member} -> remove a member from a namespace
     */
    @RequestMapping(value = "/namespaces/{name}/members/{member}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<?> removeMemberFromNamespace(@PathVariable String name, @PathVariable String member) {
        log.debug("REST request to remove '{}' from namespace '{}'", member, name);
        final Namespace ns = namespaceRepository.findOne(name);
        if (ns == null) {
            return new ResponseEntity<>(new ErrorDTO("unknown namespace"), HttpStatus.NOT_FOUND);
        } else {
            // retrieve currently logged-in user
            return userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
                .map(user -> {
                    // check that the current logged-in user owns the namespace
                    if (ns.getOwner().getLogin().equals(user.getLogin())) {
                        return userRepository.findOneByLogin(member)
                            // retrieve user to remove
                            .map(m -> namespaceRepository.findOneByNameAndMemberName(ns.getName(), m.getLogin())
                                // this user is already a member of the given namespace
                                .map(n -> {
                                    if (m.equals(user)) {
                                        return new ResponseEntity<>(new ErrorDTO("namespace owner cannot be removed from its own namespace"), HttpStatus.BAD_REQUEST);
                                    } else {
                                        // remove member from namespace
                                        ns.removeMember(m);
                                        m.removeNamespace(ns);
                                        namespaceRepository.save(ns);
                                        userRepository.save(m);
                                        return new ResponseEntity<>(HttpStatus.OK);
                                    }})
                                .orElse(new ResponseEntity<>(new ErrorDTO("not a member of the namespace"), HttpStatus.BAD_REQUEST)))
                                // cannot add unknown user to a namespace
                            .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                    } else {
                        return new ResponseEntity<>(new ErrorDTO("you are not the owner of the namespace"), HttpStatus.UNAUTHORIZED);
                    }
                })
                    // user must be logged-in
                .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
        }
    }

    /**
     * DELETE  /namespaces/{name} -> delete the "name" namespace.
     */
    @RequestMapping(value = "/namespaces/{name:.+}",
        method = RequestMethod.DELETE,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @Timed
    public ResponseEntity<?> delete(@PathVariable String name) {
        log.debug("REST request to delete Namespace : {}", name);
        if (namespaceRepository.findOne(name) == null) {
            return new ResponseEntity<>(new ErrorDTO("unable to find namespace "+name), HttpStatus.NOT_FOUND);
        } else {
            User user = userService.getUserWithAuthorities();
            Authority admin = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
            if (user.getAuthorities().contains(admin)) {
                namespaceRepository.delete(namespaceRepository.findOne(name));
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return namespaceRepository.findOneByNameAndMemberName(name, SecurityUtils.getCurrentLogin())
                    .map(ns -> {
                        if (ns.getOwner().getLogin().equals(SecurityUtils.getCurrentLogin())) {
                            namespaceRepository.delete(ns);
                            return new ResponseEntity<>(HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(new ErrorDTO("you are not the owner of "+name), HttpStatus.UNAUTHORIZED);
                        }
                    })
                    .orElse(new ResponseEntity<>(new ErrorDTO("you are not a member of "+name), HttpStatus.UNAUTHORIZED));
            }
        }
    }
}
