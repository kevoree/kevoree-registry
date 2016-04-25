package org.kevoree.registry.service;

import org.kevoree.registry.domain.Namespace;

import java.util.List;

/**
 * Service Interface for managing Namespace.
 */
public interface NamespaceService {

    /**
     * Save a namespace.
     * 
     * @param namespace the entity to save
     * @return the persisted entity
     */
    Namespace save(Namespace namespace);

    /**
     *  Get all the namespaces.
     *  
     *  @return the list of entities
     */
    List<Namespace> findAll();

    /**
     *  Get the "id" namespace.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Namespace findOne(Long id);

    /**
     *  Delete the "id" namespace.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);
}
