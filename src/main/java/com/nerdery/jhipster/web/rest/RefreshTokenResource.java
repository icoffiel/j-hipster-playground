package com.nerdery.jhipster.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nerdery.jhipster.domain.RefreshToken;
import com.nerdery.jhipster.service.RefreshTokenService;
import com.nerdery.jhipster.web.rest.dto.RefreshTokenDTO;
import com.nerdery.jhipster.web.rest.mapper.RefreshTokenMapper;
import com.nerdery.jhipster.web.rest.util.HeaderUtil;
import com.nerdery.jhipster.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing RefreshToken.
 */
@RestController
@RequestMapping("/api")
public class RefreshTokenResource {

    private final Logger log = LoggerFactory.getLogger(RefreshTokenResource.class);

    @Inject
    private RefreshTokenService refreshTokenService;

    @Inject
    private RefreshTokenMapper refreshTokenMapper;

    /**
     * POST  /refresh-tokens : Create a new refreshToken.
     *
     * @param refreshTokenDTO the refreshTokenDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new refreshTokenDTO, or with status 400 (Bad Request) if the refreshToken has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/refresh-tokens",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RefreshTokenDTO> createRefreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) throws URISyntaxException {
        log.debug("REST request to save RefreshToken : {}", refreshTokenDTO);
        if (refreshTokenDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("refreshToken", "idexists", "A new refreshToken cannot already have an ID")).body(null);
        }
        RefreshTokenDTO result = refreshTokenService.save(refreshTokenDTO);
        return ResponseEntity.created(new URI("/api/refresh-tokens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("refreshToken", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /refresh-tokens : Updates an existing refreshToken.
     *
     * @param refreshTokenDTO the refreshTokenDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated refreshTokenDTO,
     * or with status 400 (Bad Request) if the refreshTokenDTO is not valid,
     * or with status 500 (Internal Server Error) if the refreshTokenDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/refresh-tokens",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RefreshTokenDTO> updateRefreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) throws URISyntaxException {
        log.debug("REST request to update RefreshToken : {}", refreshTokenDTO);
        if (refreshTokenDTO.getId() == null) {
            return createRefreshToken(refreshTokenDTO);
        }
        RefreshTokenDTO result = refreshTokenService.save(refreshTokenDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("refreshToken", refreshTokenDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /refresh-tokens : get all the refreshTokens.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of refreshTokens in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/refresh-tokens",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<RefreshTokenDTO>> getAllRefreshTokens(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of RefreshTokens");
        Page<RefreshToken> page = refreshTokenService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/refresh-tokens");
        return new ResponseEntity<>(refreshTokenMapper.refreshTokensToRefreshTokenDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /refresh-tokens/:id : get the "id" refreshToken.
     *
     * @param id the id of the refreshTokenDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the refreshTokenDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/refresh-tokens/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RefreshTokenDTO> getRefreshToken(@PathVariable Long id) {
        log.debug("REST request to get RefreshToken : {}", id);
        RefreshTokenDTO refreshTokenDTO = refreshTokenService.findOne(id);
        return Optional.ofNullable(refreshTokenDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /refresh-tokens/:id : delete the "id" refreshToken.
     *
     * @param id the id of the refreshTokenDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/refresh-tokens/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRefreshToken(@PathVariable Long id) {
        log.debug("REST request to delete RefreshToken : {}", id);
        refreshTokenService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("refreshToken", id.toString())).build();
    }

    /**
     * SEARCH  /_search/refresh-tokens?query=:query : search for the refreshToken corresponding
     * to the query.
     *
     * @param query the query of the refreshToken search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/refresh-tokens",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<RefreshTokenDTO>> searchRefreshTokens(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of RefreshTokens for query {}", query);
        Page<RefreshToken> page = refreshTokenService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/refresh-tokens");
        return new ResponseEntity<>(refreshTokenMapper.refreshTokensToRefreshTokenDTOs(page.getContent()), headers, HttpStatus.OK);
    }

}
