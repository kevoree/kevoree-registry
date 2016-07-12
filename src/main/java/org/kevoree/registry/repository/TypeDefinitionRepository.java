package org.kevoree.registry.repository;

import org.kevoree.registry.domain.TypeDefinition;
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

    Set<TypeDefinition> findByNamespaceName(String namespaceName);

    List<TypeDefinition> findOneByNamespaceNameAndNameOrderByVersionDesc(String ns, String name);
}
