package org.kevoree.registry.service;

import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.exception.InvalidNamespaceException;
import org.kevoree.registry.exception.InvalidUserException;
import org.kevoree.registry.exception.NotTheOwnerException;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NamespaceService {

    private final Logger log = LoggerFactory.getLogger(NamespaceService.class);

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * Creates a namespace using the given fqn and sets the currently logged-in user as its owner
     * @param fqn the fqn
     * @return the created namespace
     * @throws InvalidNamespaceException
     */
    public Namespace createNamespace(String fqn)
        throws InvalidNamespaceException {
        if (fqn.matches("^([a-z_]{2,}(\\.[a-z_]+[0-9]*[a-z_]*)(\\.[a-z_]+[0-9]*[a-z_]*)*)$")) {
            List<Namespace> namespaces = namespaceRepository.findAll();
            for (Namespace ns : namespaces) {
                if (ns.getFqn().startsWith(fqn) || fqn.startsWith(ns.getFqn())) {
                    // you cannot add a sub-namespace of a previously registered namespace
                    throw new InvalidNamespaceException("Unable to create namespace with fqn \"" +
                        fqn + "\" because a fqn named \"" + ns.getFqn() + "\" already exists");
                }
            }

            Namespace ns = new Namespace();
            ns.setFqn(fqn);
            User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get();
            ns.setOwner(currentUser);
            ns.addMember(currentUser);
            currentUser.addNamespace(ns);
            namespaceRepository.save(ns);
            log.debug("Created namespace: {}", ns);
            return ns;
        } else {
            throw new InvalidNamespaceException("Invalid namespace name");
        }
    }

    /**
     * Currently logged-in user will leave the given namespace if it is not the owner
     * @param fqn the fqn
     * @throws InvalidNamespaceException
     * @throws InvalidUserException
     */
    public void leaveNamespace(String fqn)
        throws InvalidNamespaceException, InvalidUserException {
        Namespace ns = namespaceRepository.findOne(fqn);
        if (ns != null) {
            User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get();
            if (ns.getOwner().getLogin().equals(currentUser.getLogin())) {
                throw new InvalidUserException("Owner cannot leave its own namespace");
            } else {
                ns.removeMember(currentUser);
                currentUser.removeNamespace(ns);
                namespaceRepository.save(ns);
                log.debug("User \"{}\" left namespace: {}", currentUser.getLogin(), ns);
            }
        } else {
            throw new InvalidNamespaceException("Unknown namespace");
        }
    }

    /**
     * Remove a member from a namespace. Currently logged-in user must own the given namespace
     * in order for this service to perform correctly
     * @param fqn the fqn
     * @param userLogin the userLogin
     * @throws InvalidNamespaceException
     * @throws InvalidUserException
     */
    public void removeMember(String fqn, String userLogin)
        throws InvalidNamespaceException, InvalidUserException, NotTheOwnerException {
        Namespace ns = namespaceRepository.findOne(fqn);
        if (ns != null) {
            User member = userRepository.findOneByLogin(userLogin).get();
            if (member != null) {
                User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get();
                if (ns.getOwner().getLogin().equals(currentUser.getLogin())) {
                    if (member.equals(currentUser)) {
                        throw new InvalidUserException("Owner cannot leave its own namespace");
                    } else {
                        if (ns.getMembers().contains(member)) {
                            ns.removeMember(member);
                            member.removeNamespace(ns);
                            namespaceRepository.save(ns);
                            log.debug("User \"{}\" removed from namespace: {}", member.getLogin(), ns);
                        } else {
                            throw new InvalidUserException("User is not a member of this namespace");
                        }
                    }
                } else {
                    throw new NotTheOwnerException("You must own the namespace to remove members from it");
                }
            } else {
                throw new InvalidUserException("Unknown user \""+userLogin+"\"");
            }
        } else {
            throw new InvalidNamespaceException("Unknown namespace");
        }
    }

    /**
     * Add the given user to the given namespace if current logged-in user is the owner of this namespace
     * @param fqn the fqn
     * @param userLogin the userLogin
     * @throws InvalidNamespaceException
     * @throws InvalidUserException
     */
    public void addMember(String fqn, String userLogin)
        throws InvalidNamespaceException, InvalidUserException, NotTheOwnerException {
        Namespace ns = namespaceRepository.findOne(fqn);
        if (ns != null) {
            User member = userRepository.findOneByLogin(userLogin).get();
            if (member != null) {
                User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get();
                if (ns.getOwner().getLogin().equals(currentUser.getLogin())) {
                    if (ns.getMembers().contains(member)) {
                        throw new InvalidNamespaceException("User is already a member of this namespace");
                    } else {
                        ns.addMember(member);
                        member.addNamespace(ns);
                        namespaceRepository.save(ns);
                        log.debug("User \"{}\" added to namespace: {}", member.getLogin(), ns);
                    }
                } else {
                    throw new NotTheOwnerException("You must own the namespace to add members to it");
                }
            } else {
                throw new InvalidUserException("Unknown user \""+userLogin+"\"");
            }
        } else {
            throw new InvalidNamespaceException("Unknown namespace");
        }
    }
}
