package org.kevoree.registry.service;

import org.kevoree.registry.domain.Namespace;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

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

    Optional<Namespace> findOneByName(String login);

    Namespace update(Namespace namespace);
}
