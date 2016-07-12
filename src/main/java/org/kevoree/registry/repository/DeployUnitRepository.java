package org.kevoree.registry.repository;

import org.kevoree.registry.domain.DeployUnit;

import org.springframework.data.jpa.repository.*;

import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the DeployUnit entity.
 */
public interface DeployUnitRepository extends JpaRepository<DeployUnit, Long> {

    Optional<DeployUnit> findOneByTypeDefinitionIdAndNameAndVersionAndPlatform(Long tdefId, String name, String version, String platform);

    @Query("select d from DeployUnit d where d.typeDefinition in (select t from TypeDefinition t where t.namespace.name = ?1)")
    Set<DeployUnit> findByNamespace(String name);

    @Query("select d from DeployUnit d where d.typeDefinition in (select t from TypeDefinition t where t.namespace.name = ?1 and t.name = ?2)")
    Set<DeployUnit> findByNamespaceAndTypeDefinition(String nsName, String tdefName);

    @Query("select d from DeployUnit d where d.typeDefinition in (select t from TypeDefinition t where t.namespace.name = ?1 and t.name = ?2 and t.version = ?3)")
    Set<DeployUnit> findByNamespaceAndTypeDefinitionAndTypeDefinitionVersion(
        String nsName, String tdefName, Long tdefVersion);

    @Query("select d from DeployUnit d where d.typeDefinition in (select t from TypeDefinition t where t.namespace.name = ?1 and t.name = ?2 and t.version = ?3) and d.platform = ?4")
    Set<DeployUnit> findByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndPlatform(
        String nsName, String tdefName, Long tdefVersion, String platform);

    @Query("select d from DeployUnit d where d.typeDefinition in (select t from TypeDefinition t where t.namespace.name = ?1 and t.name = ?2 and t.version = ?3) and d.name = ?4")
    Set<DeployUnit> findByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndName(
        String nsName, String tdefName, Long tdefVersion, String name);

    @Query("select d from DeployUnit d where d.typeDefinition in (select t from TypeDefinition t where t.namespace.name = ?1 and t.name = ?2 and t.version = ?3) and d.name = ?4 and d.version = ?5")
    Set<DeployUnit> findByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndNameAndVersion(
        String nsName, String tdefName, Long tdefVersion, String name, String version);

    @Query("select d from DeployUnit d where d.typeDefinition in (select t from TypeDefinition t where t.namespace.name = ?1 and t.name = ?2 and t.version = ?3) and d.name = ?4 and d.version = ?5 and d.platform = ?6")
    Optional<DeployUnit> findOneByNamespaceAndTypeDefinitionAndTypeDefinitionVersionAndNameAndVersionAndPlatform(
        String nsName, String tdefName, Long tdefVersion, String name, String version, String platform);
}
