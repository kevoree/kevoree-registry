package org.kevoree.registry.repository;

import org.kevoree.registry.domain.DeployUnit;

import org.springframework.data.jpa.repository.*;

import java.util.Optional;

/**
 * Spring Data JPA repository for the DeployUnit entity.
 */
public interface DeployUnitRepository extends JpaRepository<DeployUnit, Long> {

    Optional<DeployUnit> findOneByTypeDefinitionIdAndNameAndVersionAndPlatform(Long tdefId, String name, String version, String platform);
}
