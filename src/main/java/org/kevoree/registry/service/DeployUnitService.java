package org.kevoree.registry.service;

import org.kevoree.registry.domain.DeployUnit;
import org.kevoree.registry.web.rest.dto.search.DeployUnitSearchDTO;

import java.util.List;

/**
 * Service Interface for managing DeployUnit.
 */
public interface DeployUnitService {

    /**
     * Save a deployUnit.
     *
     * @param deployUnit the entity to save
     * @return the persisted entity
     */
    DeployUnit save(DeployUnit deployUnit);

    /**
     *  Get all the deployUnits.
     *
     *  @return the list of entities
     */
    List<DeployUnit> findAll();

    /**
     *  Get the "id" deployUnit.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    DeployUnit findOne(Long id);

    /**
     *  Delete the "id" deployUnit.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    List<DeployUnit> search(DeployUnitSearchDTO deployUnitSearch);
}
