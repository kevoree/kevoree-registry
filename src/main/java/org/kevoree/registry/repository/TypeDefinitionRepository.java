package org.kevoree.registry.repository;

import org.kevoree.registry.domain.TypeDefinition;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * Created by leiko on 28/01/15.
 */
public interface TypeDefinitionRepository extends JpaRepository<TypeDefinition, Long> {

    Optional<TypeDefinition> findOneByNamespaceNameAndNameAndVersion(String ns, String name, Long version);

    Set<TypeDefinition> findByNamespaceNameAndName(String namespaceName, String name);

    @EntityGraph(attributePaths = "deployUnits")
    List<TypeDefinition> findAll();

    @EntityGraph(attributePaths = "deployUnits")
    Optional<TypeDefinition> findOneWithDeployUnitsById(Long id);

    @EntityGraph(attributePaths = "deployUnits")
    Optional<TypeDefinition> findOneWithDeployUnitsByNamespaceNameAndNameAndVersion(String namespace, String name, Long version);

    @EntityGraph(attributePaths = "deployUnits")
    Set<TypeDefinition> findAllWithDeployUnitsByNamespaceName(String namespace);

    @EntityGraph(attributePaths = "deployUnits")
    Set<TypeDefinition> findAllWithDeployUnitsByNamespaceNameAndName(String namespace, String name);

    @EntityGraph(attributePaths = "deployUnits")
    Optional<TypeDefinition> findFirst1WithDeployUnitsByNamespaceNameAndNameOrderByVersionDesc(String namespace, String name);
}
