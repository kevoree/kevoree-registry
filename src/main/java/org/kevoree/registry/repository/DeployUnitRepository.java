package org.kevoree.registry.repository;

import org.kevoree.registry.domain.DeployUnit;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the DeployUnit entity.
 */
public interface DeployUnitRepository extends JpaRepository<DeployUnit,Long>, JpaSpecificationExecutor<DeployUnit> {

}
