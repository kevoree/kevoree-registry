package org.kevoree.registry.service;

import org.kevoree.registry.config.Constants;
import org.kevoree.registry.domain.Authority;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.AuthorityRepository;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.AuthoritiesConstants;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.service.dto.UserDTO;
import org.kevoree.registry.service.util.RandomUtil;
import org.kevoree.registry.web.rest.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    public JdbcTokenStore jdbcTokenStore;

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
                .map(user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    log.debug("Activated user: {}", user);
                    return user;
                });
    }

//    public Optional<User> completePasswordReset(String newPassword, String key) {
//        log.debug("Reset user password for reset key {}", key);
//
//        return userRepository.findOneByResetKey(key)
//                .filter(user -> {
//                    ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
//                    return user.getResetDate().isAfter(oneDayAgo);
//                })
//                .map(user -> {
//                    user.setPassword(passwordEncoder.encode(newPassword));
//                    user.setResetKey(null);
//                    user.setResetDate(null);
//                    return user;
//                });
//    }

//    public Optional<User> requestPasswordReset(String mail) {
//        return userRepository.findOneByEmail(mail)
//                .filter(User::getActivated)
//                .map(user -> {
//                    user.setResetKey(RandomUtil.generateResetKey());
//                    user.setResetDate(ZonedDateTime.now());
//                    return user;
//                });
//    }

    @Transactional
    public User createUser(String login, String password, String firstName, String lastName, String email,
                           String langKey) {
        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(passwordEncoder.encode(password)); // encode password
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        newUser.setActivated(false); // not active by default
        newUser.setActivationKey(RandomUtil.generateActivationKey()); // random activation key

        // set Authority to default "USER"
        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        authorities.add(authority);
        newUser.setAuthorities(authorities);

        // save user
        userRepository.save(newUser);

        // create associated namespace
        Namespace ns = new Namespace();
        ns.setName(newUser.getLogin());
        ns.setOwner(newUser);
        ns.addMember(newUser);
        newUser.addNamespace(ns);
        namespaceRepository.save(ns);

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    @Transactional
    public User createUser(ManagedUserVM managedUserVM) {
        User user = new User();
        user.setLogin(managedUserVM.getLogin());
        user.setFirstName(managedUserVM.getFirstName());
        user.setLastName(managedUserVM.getLastName());
        user.setEmail(managedUserVM.getEmail());
        user.setPassword(passwordEncoder.encode(RandomUtil.generatePassword())); // randomly generated password
        user.setActivated(true); // activated by default
        // TODO
        //user.setResetKey(RandomUtil.generateResetKey());
        //user.setResetDate(ZonedDateTime.now());

        if (managedUserVM.getLangKey() == null) {
            user.setLangKey("en"); // default language
        } else {
            user.setLangKey(managedUserVM.getLangKey());
        }

        if (managedUserVM.getAuthorities() != null) {
            Set<Authority> authorities = new HashSet<>();
            managedUserVM.getAuthorities().forEach(
                    authority -> authorities.add(authorityRepository.findOne(authority))
            );
            user.setAuthorities(authorities);
        }

        // save user
        userRepository.save(user);

        // new user gets its own namespace
        Namespace ns = new Namespace();
        ns.setName(user.getLogin());
        ns.setOwner(user);
        ns.addMember(user);
        user.addNamespace(ns);
        namespaceRepository.save(ns);

        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void updateUser(String firstName, String lastName, String email, String langKey) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setLangKey(langKey);
            log.debug("Changed Information for User: {}", user);
        });
    }

    public void updateUser(Long id, String login, String firstName, String lastName, String email,
                           boolean activated, String langKey, Set<String> authorities) {

        Optional.of(userRepository
                .findOne(id))
                .ifPresent(user -> {
                    user.setLogin(login);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                    user.setActivated(activated);
                    user.setLangKey(langKey);
                    Set<Authority> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    authorities.forEach(
                            authority -> managedAuthorities.add(authorityRepository.findOne(authority))
                    );
                    log.debug("Changed Information for User: {}", user);
                });
    }

    public void deleteUser(final User user) {
        jdbcTokenStore.findTokensByUserName(user.getLogin()).forEach(token ->
                jdbcTokenStore.removeAccessToken(token));
        // remove membership in all namespaces
        Set<Namespace> namespaces = user.getNamespaces().stream().map(ns -> {
            ns.removeMember(user);
            user.removeNamespace(ns);
            return ns;
        }).collect(Collectors.toSet());
        namespaceRepository.save(namespaces);
        userRepository.delete(user);
        log.debug("Deleted User: {}", user);
    }

    public void changePassword(String password) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            log.debug("Changed password for User: {}", user);
        });
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLogin(login).map(user -> {
            user.getAuthorities().size();
            return user;
        });
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesAndNamespacesByLogin(String login) {
        return userRepository.findOneByLogin(login).map(user -> {
            user.getAuthorities().size();
            user.getNamespaces().size();
            return user;
        });
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities(Long id) {
        User user = userRepository.findOne(id);
        user.getAuthorities().size(); // eagerly load the association
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        Optional<User> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        User user = null;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.getAuthorities().size(); // eagerly load the association
            user.getNamespaces().size(); // eagerly load the association
        }
        return user;
    }

    public boolean hasAuthority(User user, String authority) {
        Authority auth = authorityRepository.findOne(authority);
        return auth != null && user.getAuthorities().contains(auth);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        ZonedDateTime now = ZonedDateTime.now();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user and namespace {}", user.getLogin());
            for (Namespace ns : user.getNamespaces()) {
                if (ns.getOwner().equals(user)) {
                    namespaceRepository.delete(ns);
                } else {
                    ns.removeMember(user);
                    namespaceRepository.save(ns);
                }
            }
            userRepository.delete(user);
        }
    }
}
