package org.kevoree.registry.repository.search;

import org.kevoree.registry.domain.TypeDefinition;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the TypeDefinition entity.
 */
public interface TypeDefinitionSearchRepository extends ElasticsearchRepository<TypeDefinition, Long> {
}
