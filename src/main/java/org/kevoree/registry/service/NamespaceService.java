package org.kevoree.registry.service;

import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.web.rest.dto.NamespaceSearchDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Namespace.
 */
public interface NamespaceService {

    /**
     *  Get all the namespaces.
     *
     *  @return the list of entities
     */
    List<Namespace> findAll();

    /**
     * Save a namespace.
     *
     * @param namespace the entity to save
     * @return the persisted entity
     */
    Namespace save(Namespace namespace);

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

    Namespace deactivate(Namespace namespace);

    List<Namespace> search(NamespaceSearchDTO namespaceSearch);
}
