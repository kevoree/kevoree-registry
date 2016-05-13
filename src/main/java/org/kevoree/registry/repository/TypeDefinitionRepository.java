package org.kevoree.registry.repository;

import org.kevoree.registry.domain.TypeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the TypeDefinition entity.
 */
public interface TypeDefinitionRepository extends JpaRepository<TypeDefinition, Long>, JpaSpecificationExecutor<TypeDefinition> {

    @Query("select count(typeDef.id) from TypeDefinition typeDef inner join typeDef.namespace namespace where namespace.id = :namespaceId and typeDef.name = :name and typeDef.version = :version")
    Long countSimilar(@Param("namespaceId") Long namespaceId, @Param("name") String name, @Param("version") Long version);

    @Query("select count(typeDef.id) from TypeDefinition typeDef inner join typeDef.namespace namespace where namespace.name = :namespaceName and typeDef.name = :name and typeDef.version = :version")
    Long countSimilar(@Param("namespaceName") String namespaceName, @Param("name") String name, @Param("version") Long version);
}
