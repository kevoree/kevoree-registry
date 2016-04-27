package org.kevoree.registry.service.impl;

import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.security.SecurityUtils;
import org.kevoree.registry.service.NamespaceService;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.repository.NamespaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Namespace.
 */
@Service
@Transactional
public class NamespaceServiceImpl implements NamespaceService{

    private final Logger log = LoggerFactory.getLogger(NamespaceServiceImpl.class);

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * Save a namespace.
     *
     * @param namespace the entity to save
     * @return the persisted entity
     */
    public Namespace save(final Namespace namespace) {
        log.debug("Request to save Namespace : {}", namespace);
        final Optional<User> currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        namespace.setOwner(currentUser.get());
        namespace.setActivated(true);
        return namespaceRepository.save(namespace);
    }

    @Override
    public Namespace update(Namespace namespace) {
        final Namespace fromDB = this.findOne(namespace.getId());
        fromDB.setMembers(namespace.getMembers());

        // the owner is always a member of the namespace.
        fromDB.getMembers().add(fromDB.getOwner());
        return null;
    }

    @Override
    public Namespace deactivate(Namespace namespace) {
        namespace.setActivated(false);
        final Namespace ret = this.namespaceRepository.save(namespace);
        return ret;
    }


    /**
     *  Get all the namespaces.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Namespace> findAll() {
        log.debug("Request to get all Namespaces");
        List<Namespace> result = namespaceRepository.findAllWithEagerRelationships();
        return result;
    }

    /**
     *  Get one namespace by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Namespace findOne(Long id) {
        log.debug("Request to get Namespace : {}", id);
        Namespace namespace = namespaceRepository.findOneWithEagerRelationships(id);
        return namespace;
    }

    /**
     *  Delete the  namespace by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Namespace : {}", id);
        namespaceRepository.delete(id);
    }

    @Override
    public Optional<Namespace> findOneByName(String login) {
        final Namespace oneByName = namespaceRepository.findOneByName(login);
        return Optional.ofNullable(oneByName);
    }


}
