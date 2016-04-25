package org.kevoree.registry.service;

import org.kevoree.registry.domain.TypeDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing TypeDefinition.
 */
public interface TypeDefinitionService {

    /**
     * Save a typeDefinition.
     * 
     * @param typeDefinition the entity to save
     * @return the persisted entity
     */
    TypeDefinition save(TypeDefinition typeDefinition);

    /**
     *  Get all the typeDefinitions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<TypeDefinition> findAll(Pageable pageable);

    /**
     *  Get the "id" typeDefinition.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    TypeDefinition findOne(Long id);

    /**
     *  Delete the "id" typeDefinition.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);
}
