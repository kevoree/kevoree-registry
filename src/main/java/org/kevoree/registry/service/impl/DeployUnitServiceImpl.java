package org.kevoree.registry.service.impl;

import org.kevoree.registry.service.DeployUnitService;
import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.repository.DeployUnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing DeployUnit.
 */
@Service
@Transactional
public class DeployUnitServiceImpl implements DeployUnitService{

    private final Logger log = LoggerFactory.getLogger(DeployUnitServiceImpl.class);
    
    @Inject
    private DeployUnitRepository deployUnitRepository;
    
    /**
     * Save a deployUnit.
     * 
     * @param deployUnit the entity to save
     * @return the persisted entity
     */
    public DeployUnit save(DeployUnit deployUnit) {
        log.debug("Request to save DeployUnit : {}", deployUnit);
        DeployUnit result = deployUnitRepository.save(deployUnit);
        return result;
    }

    /**
     *  Get all the deployUnits.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<DeployUnit> findAll() {
        log.debug("Request to get all DeployUnits");
        List<DeployUnit> result = deployUnitRepository.findAll();
        return result;
    }

    /**
     *  Get one deployUnit by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public DeployUnit findOne(Long id) {
        log.debug("Request to get DeployUnit : {}", id);
        DeployUnit deployUnit = deployUnitRepository.findOne(id);
        return deployUnit;
    }

    /**
     *  Delete the  deployUnit by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete DeployUnit : {}", id);
        deployUnitRepository.delete(id);
    }
}
