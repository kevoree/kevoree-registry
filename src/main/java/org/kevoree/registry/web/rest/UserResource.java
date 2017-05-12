package org.kevoree.registry.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiParam;
import org.kevoree.registry.config.Constants;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.service.MailService;
import org.kevoree.registry.service.UserService;
import org.kevoree.registry.service.dto.UserDTO;
import org.kevoree.registry.web.rest.util.HeaderUtil;
import org.kevoree.registry.web.rest.util.PaginationUtil;
import org.kevoree.registry.web.rest.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for managing users.
 *
 * <p>This class accesses the User entity, and needs to fetch its collection of authorities.</p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>Another option would be to have a specific JPA entity graph to handle this case.</p>
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private MailService mailService;

    @Inject
    private UserService userService;

    /**
     * POST  /users  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     * </p>
     *
     * @param managedUserVM the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> createUser(@RequestBody ManagedUserVM managedUserVM) throws URISyntaxException {
        log.debug("REST request to save User : {}", managedUserVM);

        //Lowercase the user login before comparing with database
        if (userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("userManagement.error.loginExists"))
                    .body(null);
        } else if (userRepository.findOneByEmail(managedUserVM.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("userManagement.error.emailExists"))
                    .body(null);
        } else {
            User newUser = userService.createUser(managedUserVM);
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                    .headers(HeaderUtil.createAlert( "userManagement.created", newUser.getLogin()))
                    .body(newUser);
        }
    }

    /**
     * PUT  /users : Updates an existing User.
     *
     * @param managedUserVM the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the login or email is already in use,
     * or with status 500 (Internal Server Error) if the user couldn't be updated
     */
    @PutMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<ManagedUserVM> updateUser(@RequestBody ManagedUserVM managedUserVM) {
        log.debug("REST request to update User : {}", managedUserVM);
        Optional<User> existingUser = userRepository.findOneByEmail(managedUserVM.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("userManagement.error.emailExists"))
                    .body(null);
        }
        existingUser = userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("userManagement.error.loginExists"))
                    .body(null);
        }
        userService.updateUser(managedUserVM.getId(), managedUserVM.getLogin(), managedUserVM.getFirstName(),
                managedUserVM.getLastName(), managedUserVM.getEmail(), managedUserVM.isActivated(),
                managedUserVM.getLangKey(), managedUserVM.getAuthorities());

        return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert("userManagement.updated", managedUserVM.getLogin()))
                .body(new ManagedUserVM(userService.getUserWithAuthorities(managedUserVM.getId())));
    }

    /**
     * GET  /users : get all users.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body page with all users
     */
    @GetMapping("/users")
    @Timed
    public ResponseEntity<List<UserDTO>> getAllUsers(@ApiParam Pageable pageable) {
        final Page<UserDTO> page = userService.getAllManagedUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public ResponseEntity<ManagedUserVM> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return userService.getUserWithAuthoritiesAndNamespacesByLogin(login)
                .map(ManagedUserVM::new)
                .map(managedUserVM -> new ResponseEntity<>(managedUserVM, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE /users/:login : delete the "login" User.
     *
     * @param login the login of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
        return userRepository.findOneWithNamespacesByLogin(login)
                .map(user -> {
                    if (user.ownsNamespaces()) {
                        return ResponseEntity.badRequest()
                                .headers(HeaderUtil.createFailureAlert("userManagement.error.deleteOwner"))
                                .build();
                    } else {
                        userService.deleteUser(user);
                        return ResponseEntity.ok()
                                .headers(HeaderUtil.createAlert( "userManagement.deleted", login))
                                .build();
                    }
                }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET  /user/namespaces -> get the user's namespaces
     */
    @GetMapping("/user/namespaces")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    ResponseEntity<Set<Namespace>> getUserNamespaces() {
        log.debug("REST request to get User '{}' namespaces", SecurityUtils.getCurrentUserLogin());
        return new ResponseEntity<>(namespaceRepository.findByOwnerIsCurrentUser(), HttpStatus.OK);
    }

    /**
     * GET  /user/tdefs -> get the user's typedefinitions
     */
    @GetMapping("/user/tdefs")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    ResponseEntity<Set<TypeDefinition>> getUserTypeDefinitions() {
        log.debug("REST request to get User '{}' typedefinitions", SecurityUtils.getCurrentUserLogin());
        return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin())
                .map(user -> {
                    Set<TypeDefinition> tdefs = new HashSet<>();
                    user.getNamespaces().forEach(ns -> tdefs.addAll(ns.getTypeDefinitions()));
                    return new ResponseEntity<>(tdefs, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }
}
