package org.kevoree.registry.repository.search;

import org.kevoree.registry.domain.DeployUnit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the DeployUnit entity.
 */
public interface DeployUnitSearchRepository extends ElasticsearchRepository<DeployUnit, Long> {
}
