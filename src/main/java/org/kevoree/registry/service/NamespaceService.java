package org.kevoree.registry.service;

import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.service.mapper.NamespaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Service class for managing namespaces.
 */
@Service
@Transactional
public class NamespaceService {

    private final Logger log = LoggerFactory.getLogger(NamespaceService.class);

    @Inject
    private NamespaceRepository namespaceRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private NamespaceMapper namespaceMapper;

    public Namespace create(String name, User user) {
        Namespace newNs = new Namespace();
        newNs.setName(name);
        newNs.setOwner(user);
        newNs.addMember(user);
        user.addNamespace(newNs);
        namespaceRepository.save(newNs);
        userRepository.save(user);
        return newNs;
    }
}
