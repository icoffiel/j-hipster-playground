package com.nerdery.jhipster.service;

import com.nerdery.jhipster.domain.RefreshToken;
import com.nerdery.jhipster.web.rest.dto.RefreshTokenDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing RefreshToken.
 */
public interface RefreshTokenService {

    /**
     * Save a refreshToken.
     *
     * @param refreshTokenDTO the entity to save
     * @return the persisted entity
     */
    RefreshTokenDTO save(RefreshTokenDTO refreshTokenDTO);

    /**
     *  Get all the refreshTokens.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<RefreshToken> findAll(Pageable pageable);

    /**
     *  Get the "id" refreshToken.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    RefreshTokenDTO findOne(Long id);

    /**
     *  Delete the "id" refreshToken.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Get the "token" refreshToken.
     *
     * @param token The token of the entity.
     * @return the entity.
     */
    RefreshTokenDTO findOneByToken(String token);

    /**
     * Search for the refreshToken corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<RefreshToken> search(String query, Pageable pageable);
}
