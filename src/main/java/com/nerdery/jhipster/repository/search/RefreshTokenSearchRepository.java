package com.nerdery.jhipster.repository.search;

import com.nerdery.jhipster.domain.RefreshToken;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the RefreshToken entity.
 */
public interface RefreshTokenSearchRepository extends ElasticsearchRepository<RefreshToken, Long> {
}
