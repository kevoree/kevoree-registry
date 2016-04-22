package org.kevoree.registry.repository;

import org.kevoree.registry.domain.Namespace;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Namespace entity.
 */
public interface NamespaceRepository extends JpaRepository<Namespace,Long> {

    @Query("select namespace from Namespace namespace where namespace.owner.login = ?#{principal}")
    List<Namespace> findByOwnerIsCurrentUser();

    @Query("select distinct namespace from Namespace namespace left join fetch namespace.members")
    List<Namespace> findAllWithEagerRelationships();

    @Query("select namespace from Namespace namespace left join fetch namespace.members where namespace.id =:id")
    Namespace findOneWithEagerRelationships(@Param("id") Long id);

}