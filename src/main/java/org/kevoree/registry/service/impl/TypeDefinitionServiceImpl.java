package org.kevoree.registry.service.impl;

import org.kevoree.registry.service.TypeDefinitionService;
import org.kevoree.registry.domain.TypeDefinition;
import org.kevoree.registry.repository.TypeDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing TypeDefinition.
 */
@Service
@Transactional
public class TypeDefinitionServiceImpl implements TypeDefinitionService{

    private final Logger log = LoggerFactory.getLogger(TypeDefinitionServiceImpl.class);
    
    @Inject
    private TypeDefinitionRepository typeDefinitionRepository;
    
    /**
     * Save a typeDefinition.
     * 
     * @param typeDefinition the entity to save
     * @return the persisted entity
     */
    public TypeDefinition save(TypeDefinition typeDefinition) {
        log.debug("Request to save TypeDefinition : {}", typeDefinition);
        TypeDefinition result = typeDefinitionRepository.save(typeDefinition);
        return result;
    }

    /**
     *  Get all the typeDefinitions.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<TypeDefinition> findAll(Pageable pageable) {
        log.debug("Request to get all TypeDefinitions");
        Page<TypeDefinition> result = typeDefinitionRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one typeDefinition by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public TypeDefinition findOne(Long id) {
        log.debug("Request to get TypeDefinition : {}", id);
        TypeDefinition typeDefinition = typeDefinitionRepository.findOne(id);
        return typeDefinition;
    }

    /**
     *  Delete the  typeDefinition by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete TypeDefinition : {}", id);
        typeDefinitionRepository.delete(id);
    }
}
