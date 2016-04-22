package org.kevoree.registry.repository;

import org.kevoree.registry.domain.TypeDefinition;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TypeDefinition entity.
 */
public interface TypeDefinitionRepository extends JpaRepository<TypeDefinition,Long> {

}
