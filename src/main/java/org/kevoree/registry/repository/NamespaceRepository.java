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

    Namespace findOneByName(String name);


    List<Namespace> findAllByNameContaining(String search);

    List<Namespace> findAllByNameStartingWith(String search);

    List<Namespace> findAllByNameEndingWith(String search);

    List<Namespace> findAllByNameLike(String namespace);

    @Query("select count(namespace.id) from Namespace namespace where namespace.name = :name")
    Long countSimilar(@Param("name") String name);
}
