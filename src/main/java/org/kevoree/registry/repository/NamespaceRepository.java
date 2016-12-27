package org.kevoree.registry.repository;

import org.kevoree.registry.domain.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 *
 * Created by leiko on 28/01/15.
 */
public interface NamespaceRepository extends JpaRepository<Namespace, String> {

    @Query("select n from Namespace n, IN (n.members) AS m where n.name = ?1 and m.login = ?2")
    Optional<Namespace> findOneByNameAndMemberName(String name, String userName);
}
