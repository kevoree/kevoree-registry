package org.kevoree.registry.service.impl;

import org.kevoree.registry.service.NamespaceService;
import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.repository.NamespaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Namespace.
 */
@Service
@Transactional
public class NamespaceServiceImpl implements NamespaceService{

    private final Logger log = LoggerFactory.getLogger(NamespaceServiceImpl.class);
    
    @Inject
    private NamespaceRepository namespaceRepository;
    
    /**
     * Save a namespace.
     * 
     * @param namespace the entity to save
     * @return the persisted entity
     */
    public Namespace save(Namespace namespace) {
        log.debug("Request to save Namespace : {}", namespace);
        Namespace result = namespaceRepository.save(namespace);
        return result;
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
}
