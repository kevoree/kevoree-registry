package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiParam;
import org.kevoree.registry.config.Constants;
import org.kevoree.registry.domain.Authority;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.AuthorityRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.service.NamespaceService;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.service.dto.ErrorDTO;
import org.kevoree.registry.service.dto.NamedDTO;
import org.kevoree.registry.service.dto.NamespaceDTO;
import org.kevoree.registry.service.dto.UserDTO;
import org.kevoree.registry.service.mapper.NamespaceMapper;
import org.kevoree.registry.web.rest.util.PaginationUtil;
import org.kevoree.registry.web.rest.vm.NamespaceDetailVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private NamespaceService namespaceService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private NamespaceMapper namespaceMapper;

    /**
     * GET  /namespaces -> get all namespaces
     */
    @GetMapping("/namespaces")
    @Timed
    public ResponseEntity<List<NamespaceDetailVM>> getNamespaces(@ApiParam Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get pageable namespaces for user: {}", SecurityUtils.getCurrentUserLogin());
        Page<Namespace> page = namespaceRepository.findAll(pageable);
        List<NamespaceDetailVM> namespaceDTOs = page.getContent()
                .stream()
                .map(NamespaceDetailVM::new)
                .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/namespaces");
        return new ResponseEntity<>(namespaceDTOs, headers, HttpStatus.OK);
    }

    /**
     * GET  /namespaces/:name -> get a precise namespace
     */
    @GetMapping("/namespaces/{name:" + Constants.NS_NAME_REGEX + "}")
    @Timed
    @Transactional
    public ResponseEntity<NamespaceDTO> getNamespace(@PathVariable String name) {
        log.debug("REST request to get namespace: {}", name);
        return Optional.ofNullable(namespaceRepository.findOne(name))
                .map(namespace -> {
                    namespace.getTypeDefinitions().size();
                    return new ResponseEntity<>(namespaceMapper.namespaceToNamespaceDTO(namespace), HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /namespaces/:name/members -> get namespace members
     */
    @GetMapping("/namespaces/{name:" + Constants.NS_NAME_REGEX + "}/members")
    @Timed
    public ResponseEntity<List<String>> getNamespaceMembers(@PathVariable String name) {
        log.debug("REST request to get namespace members: {}", name);
        return Optional.ofNullable(namespaceRepository.findOne(name))
                .map(namespace -> new ResponseEntity<>(namespace.getMembers().stream()
                        .map(User::getLogin).collect(Collectors.toList()), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /namespaces/:name/owner -> get namespace's owner
     */
    @GetMapping("/namespaces/{name:" + Constants.NS_NAME_REGEX + "}/owner")
    @Timed
    public ResponseEntity<UserDTO> getNamespaceOwner(@PathVariable String name) {
        log.debug("REST request to get namespace owner: {}", name);
        return Optional.ofNullable(namespaceRepository.findOne(name))
                .map(namespace -> new ResponseEntity<>(new UserDTO(namespace.getOwner()), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST  /namespaces -> add a namespace
     */
    @PostMapping("/namespaces")
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> addNamespaces(@Valid @RequestBody NamedDTO namedDTO) {
        log.debug("REST request to add a namespace: {}", namedDTO);
        Optional<Namespace> oNs = Optional.ofNullable(namespaceRepository.findOne(namedDTO.getName()));
        if (oNs.isPresent()) {
            // namespace already exists
            return new ResponseEntity<>(new ErrorDTO("namespace already in use"), HttpStatus.BAD_REQUEST);
        } else {
            // namespace is available: retrieve authenticated user
            User user = userService.getUserWithAuthorities();
            return userRepository.findOneByLogin(namedDTO.getName())
                    .map(userWithSameNameThatNs -> {
                        // there is already a user with the same name as the namespace
                        if (userWithSameNameThatNs.getLogin().equals(user.getLogin())) {
                            // current authenticated user is this user: it's fine
                            Namespace newNs = namespaceService.create(namedDTO.getName(), user);
                            return new ResponseEntity<>(newNs, HttpStatus.CREATED);
                        } else {
                            // current authenticated user is not that user: forbidden
                            return new ResponseEntity<>(
                                    new ErrorDTO("only the user \"" + user.getLogin() + "\" can create this namespace"),
                                    HttpStatus.FORBIDDEN);
                        }
                    })
                    .orElseGet(() -> {
                        Namespace newNs = namespaceService.create(namedDTO.getName(), user);
                        return new ResponseEntity<>(newNs, HttpStatus.CREATED);
                    });
        }
    }

    /**
     * POST  /namespace/{name}/members -> add a member to a namespace
     */
    @PostMapping("/namespaces/{name:" + Constants.NS_NAME_REGEX + "}/members")
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity addMemberToNamespace(@PathVariable String name, @Valid @RequestBody NamedDTO namedDTO) {
        log.debug("REST request to add '{}' to namespace '{}'", namedDTO, name);
        return Optional.of(namespaceRepository.findOne(name))
                .map(ns -> {
                    // retrieve currently logged-in user
                    return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin())
                            .map(user -> {
                                // check that the current logged-in user owns the namespace or is admin
                                if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)
                                        || ns.getOwner().getLogin().equals(user.getLogin())) {
                                    return userRepository.findOneWithNamespacesByLogin(namedDTO.getName())
                                            // retrieve user to add as a member from the db
                                            .map(member -> namespaceRepository.findOneByNameAndMemberName(ns.getName(), member.getLogin())
                                                    // this user is already a member of the given namespace
                                                    .map(n -> new ResponseEntity<>(HttpStatus.BAD_REQUEST))
                                                    .orElseGet(() -> {
                                                        // add member to namespace
                                                        ns.addMember(member);
                                                        member.addNamespace(ns);
                                                        namespaceRepository.save(ns);
                                                        userRepository.save(member);
                                                        return new ResponseEntity<>(new NamespaceDTO(namespaceRepository.findOneWithTypeDefinitionsByName(ns.getName())), HttpStatus.OK);
                                                    }))
                                            // cannot add unknown user to a namespace
                                            .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                                } else {
                                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                                }
                            })
                            // user must be logged-in
                            .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /namespace/{name}/members/{member} -> remove a member from a namespace
     */
    @DeleteMapping("/namespaces/{name:" + Constants.NS_NAME_REGEX + "}/members/{member:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> removeMemberFromNamespace(@PathVariable String name, @PathVariable String member) {
        log.debug("REST request to remove '{}' from namespace '{}'", member, name);
        final Namespace ns = namespaceRepository.findOne(name);
        if (ns == null) {
            return new ResponseEntity<>(new ErrorDTO("unknown namespace"), HttpStatus.NOT_FOUND);
        } else {
            // retrieve currently logged-in user
            return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin())
                    .map(user -> {
                        // check that the current logged-in user owns the namespace
                        if (ns.getOwner().getLogin().equals(user.getLogin())) {
                            return userRepository.findOneWithNamespacesByLogin(member)
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
    @DeleteMapping("/namespaces/{name:" + Constants.NS_NAME_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<?> delete(@PathVariable String name) {
        log.debug("REST user \"{}\" requested to delete Namespace : {}", SecurityUtils.getCurrentUserLogin(), name);
        if (namespaceRepository.findOne(name) == null) {
            return new ResponseEntity<>(new ErrorDTO("unable to find namespace "+name), HttpStatus.NOT_FOUND);
        } else {
            User user = userService.getUserWithAuthorities();
            Authority admin = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
            if (user.getAuthorities().contains(admin)) {
                namespaceRepository.delete(namespaceRepository.findOne(name));
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return namespaceRepository.findOneByNameAndOwnerLogin(name, SecurityUtils.getCurrentUserLogin())
                        .map(ns -> {
                            namespaceRepository.delete(ns);
                            return new ResponseEntity<>(HttpStatus.OK);
                        })
                        .orElse(new ResponseEntity<>(new ErrorDTO("you are not the owner of "+name), HttpStatus.UNAUTHORIZED));
            }
        }
    }
}
