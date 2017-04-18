package org.kevoree.registry.repository.search;

import org.kevoree.registry.domain.Namespace;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Namespace entity.
 */
public interface NamespaceSearchRepository extends ElasticsearchRepository<Namespace, String> {
}
