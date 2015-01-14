package org.kevoree.registry.repository;

import org.kevoree.registry.domain.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * Spring Data JPA repository for the Namespace entity.
 */
public interface NamespaceRepository extends JpaRepository<Namespace, String> {

    @Query("select n from Namespace n where n.fqn like concat('.%',?1)")
    Set<Namespace> getNamespaceThatStartWith(String fqn);
}
