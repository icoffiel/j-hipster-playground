package com.nerdery.jhipster.service.impl;

import com.nerdery.jhipster.domain.RefreshToken;
import com.nerdery.jhipster.repository.RefreshTokenRepository;
import com.nerdery.jhipster.repository.search.RefreshTokenSearchRepository;
import com.nerdery.jhipster.service.RefreshTokenService;
import com.nerdery.jhipster.web.rest.dto.RefreshTokenDTO;
import com.nerdery.jhipster.web.rest.mapper.RefreshTokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing RefreshToken.
 */
@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final Logger log = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    @Inject
    private RefreshTokenRepository refreshTokenRepository;

    @Inject
    private RefreshTokenMapper refreshTokenMapper;

    @Inject
    private RefreshTokenSearchRepository refreshTokenSearchRepository;

    /**
     * Save a refreshToken.
     *
     * @param refreshTokenDTO the entity to save
     * @return the persisted entity
     */
    public RefreshTokenDTO save(RefreshTokenDTO refreshTokenDTO) {
        log.debug("Request to save RefreshToken : {}", refreshTokenDTO);
        RefreshToken refreshToken = refreshTokenMapper.refreshTokenDTOToRefreshToken(refreshTokenDTO);
        refreshToken = refreshTokenRepository.save(refreshToken);
        RefreshTokenDTO result = refreshTokenMapper.refreshTokenToRefreshTokenDTO(refreshToken);
        refreshTokenSearchRepository.save(refreshToken);
        return result;
    }

    /**
     *  Get all the refreshTokens.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<RefreshToken> findAll(Pageable pageable) {
        log.debug("Request to get all RefreshTokens");
        Page<RefreshToken> result = refreshTokenRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one refreshToken by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public RefreshTokenDTO findOne(Long id) {
        log.debug("Request to get RefreshToken : {}", id);
        RefreshToken refreshToken = refreshTokenRepository.findOne(id);
        RefreshTokenDTO refreshTokenDTO = refreshTokenMapper.refreshTokenToRefreshTokenDTO(refreshToken);
        return refreshTokenDTO;
    }

    /**
     *  Delete the  refreshToken by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete RefreshToken : {}", id);
        refreshTokenRepository.delete(id);
        refreshTokenSearchRepository.delete(id);
    }

    @Override
    public RefreshTokenDTO findOneByToken(String token) {
        log.debug("Request to get RefreshToken: {}", token);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);
        return refreshTokenMapper.refreshTokenToRefreshTokenDTO(refreshToken);
    }

    /**
     * Search for the refreshToken corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<RefreshToken> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of RefreshTokens for query {}", query);
        return refreshTokenSearchRepository.search(queryStringQuery(query), pageable);
    }
}
