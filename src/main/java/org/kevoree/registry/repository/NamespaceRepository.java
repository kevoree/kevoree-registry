package org.kevoree.registry.repository;

import org.kevoree.registry.domain.Namespace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * Created by leiko on 28/01/15.
 */
public interface NamespaceRepository extends JpaRepository<Namespace, String> {

    @Override
    @EntityGraph(attributePaths = { "members", "tdefs" })
    Page<Namespace> findAll(Pageable pageable);

    @Query("select namespace from Namespace namespace where namespace.owner.login = ?#{principal.username}")
    Set<Namespace> findByOwnerIsCurrentUser();

    @EntityGraph(attributePaths = { "tdefs", "members" })
    Namespace findOneWithTypeDefinitionsByName(String name);

    @EntityGraph(attributePaths = { "tdefs", "members" })
    @Query("select n from Namespace n, IN (n.members) AS m where n.name = ?1 and m.login = ?2")
    Optional<Namespace> findOneWithMembersAndTypeDefinitionsByNameAndMemberName(String name, String memberName);

    @Query("select n from Namespace n, IN (n.members) AS m where n.name = ?1 and m.login = ?2")
    Optional<Namespace> findOneByNameAndMemberName(String name, String userName);

    @Query("select n from Namespace n, IN (n.owner) AS o where n.name = ?1 and o.login = ?2")
    Optional<Namespace> findOneByNameAndOwnerLogin(String name, String ownerName);

    @EntityGraph(attributePaths = { "tdefs", "members" })
    Namespace findOneWithMembersAndTypeDefinitionsByName(String name);
}
