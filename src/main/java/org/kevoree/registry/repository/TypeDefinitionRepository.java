package org.kevoree.registry.repository;

import org.kevoree.registry.domain.TypeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

/**
 *
 * Created by leiko on 28/01/15.
 */
public interface TypeDefinitionRepository extends JpaRepository<TypeDefinition, Long> {

    Optional<TypeDefinition> findOneByNamespaceNameAndNameAndVersion(String ns, String name, String version);

    Optional<TypeDefinition> findOneByNamespaceNameAndNamespaceMembersLoginAndNameAndVersion(String ns, String memberLogin, String name, String version);

    Set<TypeDefinition> findByNamespaceNameAndNamespaceMembersLoginAndName(String ns, String memberLogin, String name);

    Set<TypeDefinition> findByNamespaceNameAndName(String namespaceName, String name);

    Set<TypeDefinition> findByNamespaceName(String namespaceName);
}
