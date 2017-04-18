package org.kevoree.registry.service;

import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.User;
import org.kevoree.registry.repository.NamespaceRepository;
import org.kevoree.registry.repository.UserRepository;
import org.kevoree.registry.repository.search.NamespaceSearchRepository;
import org.kevoree.registry.service.dto.NamespaceDTO;
import org.kevoree.registry.service.mapper.NamespaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.elasticsearch.index.query.QueryBuilders.*;

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
    private NamespaceSearchRepository namespaceSearchRepository;

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

    /**
     * Search for the namespace corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<NamespaceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Namespaces for query {}", query);
        Page<Namespace> result = namespaceSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(namespaceMapper::namespaceToNamespaceDTO);
    }
}
