package org.kevoree.registry.repository;

import org.kevoree.registry.domain.TypeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 *
 * Created by leiko on 28/01/15.
 */
public interface TypeDefinitionRepository extends JpaRepository<TypeDefinition, Long> {

    @Query("select t from TypeDefinition t where t.namespace.name = ?1 and t.name = ?2 and t.version = ?3")
    Optional<TypeDefinition> findOneByNamespaceNameAndNameAndVersion(String namespaceName, String name, String version);
}
