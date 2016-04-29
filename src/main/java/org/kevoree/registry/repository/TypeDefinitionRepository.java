package org.kevoree.registry.repository;

import org.kevoree.registry.domain.Namespace;
import org.kevoree.registry.domain.TypeDefinition;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the TypeDefinition entity.
 */
public interface TypeDefinitionRepository extends JpaRepository<TypeDefinition,Long>, JpaSpecificationExecutor<TypeDefinition> {

    //@Query("select namespace from Namespace namespace left join fetch namespace.members where namespace.id =:id")
    //Namespace findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select count(typeDef.id) from TypeDefinition typeDef inner join typeDef.namespace namespace where namespace.id = :namespaceId and typeDef.name = :name and typeDef.version = :version")
    Long countSimilar(@Param("namespaceId") Long namespaceId, @Param("name") String name, @Param("version") Long version);
}
