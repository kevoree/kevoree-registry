package org.kevoree.registry.repository;

import org.kevoree.registry.domain.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

/**
 *
 * Created by leiko on 28/01/15.
 */
public interface NamespaceRepository extends JpaRepository<Namespace, String> {

    @Query("select namespace from Namespace namespace where namespace.owner.login = ?#{principal.username}")
    Set<Namespace> findByOwnerIsCurrentUser();

//    @Query("select namespace from Namespace namespace where namespace.members.login = ?#{principal.username}")
//    Set<Namespace> findByMemberIsCurrentUser();

    @Query("select n from Namespace n left join fetch n.tdefs where n.name = ?1")
    Namespace findOneWithTypeDefinitionsByName(String name);

    @Query("select n from Namespace n, IN (n.members) AS m where n.name = ?1 and m.login = ?2")
    Optional<Namespace> findOneByNameAndMemberName(String name, String userName);

    @Query("select n from Namespace n, IN (n.owner) AS o where n.name = ?1 and o.login = ?2")
    Optional<Namespace> findOneByNameAndOwnerLogin(String name, String ownerName);
}
