package com.nerdery.jhipster.web.rest;

import com.nerdery.jhipster.JHipsterPlaygroundApp;
import com.nerdery.jhipster.domain.RefreshToken;
import com.nerdery.jhipster.repository.RefreshTokenRepository;
import com.nerdery.jhipster.repository.search.RefreshTokenSearchRepository;
import com.nerdery.jhipster.service.RefreshTokenService;
import com.nerdery.jhipster.web.rest.dto.RefreshTokenDTO;
import com.nerdery.jhipster.web.rest.mapper.RefreshTokenMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the RefreshTokenResource REST controller.
 *
 * @see RefreshTokenResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JHipsterPlaygroundApp.class)
@WebAppConfiguration
@IntegrationTest
public class RefreshTokenResourceIntTest {

    private static final String DEFAULT_TOKEN = "AAAAA";
    private static final String UPDATED_TOKEN = "BBBBB";

    private static final Boolean DEFAULT_EXPIRED = false;
    private static final Boolean UPDATED_EXPIRED = true;

    @Inject
    private RefreshTokenRepository refreshTokenRepository;

    @Inject
    private RefreshTokenMapper refreshTokenMapper;

    @Inject
    private RefreshTokenService refreshTokenService;

    @Inject
    private RefreshTokenSearchRepository refreshTokenSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRefreshTokenMockMvc;

    private RefreshToken refreshToken;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RefreshTokenResource refreshTokenResource = new RefreshTokenResource();
        ReflectionTestUtils.setField(refreshTokenResource, "refreshTokenService", refreshTokenService);
        ReflectionTestUtils.setField(refreshTokenResource, "refreshTokenMapper", refreshTokenMapper);
        this.restRefreshTokenMockMvc = MockMvcBuilders.standaloneSetup(refreshTokenResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        refreshTokenSearchRepository.deleteAll();
        refreshToken = new RefreshToken();
        refreshToken.setToken(DEFAULT_TOKEN);
        refreshToken.setExpired(DEFAULT_EXPIRED);
    }

    @Test
    @Transactional
    public void createRefreshToken() throws Exception {
        int databaseSizeBeforeCreate = refreshTokenRepository.findAll().size();

        // Create the RefreshToken
        RefreshTokenDTO refreshTokenDTO = refreshTokenMapper.refreshTokenToRefreshTokenDTO(refreshToken);

        restRefreshTokenMockMvc.perform(post("/api/refresh-tokens")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(refreshTokenDTO)))
                .andExpect(status().isCreated());

        // Validate the RefreshToken in the database
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(databaseSizeBeforeCreate + 1);
        RefreshToken testRefreshToken = refreshTokens.get(refreshTokens.size() - 1);
        assertThat(testRefreshToken.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testRefreshToken.isExpired()).isEqualTo(DEFAULT_EXPIRED);

        // Validate the RefreshToken in ElasticSearch
        RefreshToken refreshTokenEs = refreshTokenSearchRepository.findOne(testRefreshToken.getId());
        assertThat(refreshTokenEs).isEqualToComparingFieldByField(testRefreshToken);
    }

    @Test
    @Transactional
    public void checkTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = refreshTokenRepository.findAll().size();
        // set the field null
        refreshToken.setToken(null);

        // Create the RefreshToken, which fails.
        RefreshTokenDTO refreshTokenDTO = refreshTokenMapper.refreshTokenToRefreshTokenDTO(refreshToken);

        restRefreshTokenMockMvc.perform(post("/api/refresh-tokens")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(refreshTokenDTO)))
                .andExpect(status().isBadRequest());

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRefreshTokens() throws Exception {
        // Initialize the database
        refreshTokenRepository.saveAndFlush(refreshToken);

        // Get all the refreshTokens
        restRefreshTokenMockMvc.perform(get("/api/refresh-tokens?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(refreshToken.getId().intValue())))
                .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN.toString())))
                .andExpect(jsonPath("$.[*].expired").value(hasItem(DEFAULT_EXPIRED.booleanValue())));
    }

    @Test
    @Transactional
    public void getRefreshToken() throws Exception {
        // Initialize the database
        refreshTokenRepository.saveAndFlush(refreshToken);

        // Get the refreshToken
        restRefreshTokenMockMvc.perform(get("/api/refresh-tokens/{id}", refreshToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(refreshToken.getId().intValue()))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN.toString()))
            .andExpect(jsonPath("$.expired").value(DEFAULT_EXPIRED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRefreshToken() throws Exception {
        // Get the refreshToken
        restRefreshTokenMockMvc.perform(get("/api/refresh-tokens/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRefreshToken() throws Exception {
        // Initialize the database
        refreshTokenRepository.saveAndFlush(refreshToken);
        refreshTokenSearchRepository.save(refreshToken);
        int databaseSizeBeforeUpdate = refreshTokenRepository.findAll().size();

        // Update the refreshToken
        RefreshToken updatedRefreshToken = new RefreshToken();
        updatedRefreshToken.setId(refreshToken.getId());
        updatedRefreshToken.setToken(UPDATED_TOKEN);
        updatedRefreshToken.setExpired(UPDATED_EXPIRED);
        RefreshTokenDTO refreshTokenDTO = refreshTokenMapper.refreshTokenToRefreshTokenDTO(updatedRefreshToken);

        restRefreshTokenMockMvc.perform(put("/api/refresh-tokens")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(refreshTokenDTO)))
                .andExpect(status().isOk());

        // Validate the RefreshToken in the database
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(databaseSizeBeforeUpdate);
        RefreshToken testRefreshToken = refreshTokens.get(refreshTokens.size() - 1);
        assertThat(testRefreshToken.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testRefreshToken.isExpired()).isEqualTo(UPDATED_EXPIRED);

        // Validate the RefreshToken in ElasticSearch
        RefreshToken refreshTokenEs = refreshTokenSearchRepository.findOne(testRefreshToken.getId());
        assertThat(refreshTokenEs).isEqualToComparingFieldByField(testRefreshToken);
    }

    @Test
    @Transactional
    public void deleteRefreshToken() throws Exception {
        // Initialize the database
        refreshTokenRepository.saveAndFlush(refreshToken);
        refreshTokenSearchRepository.save(refreshToken);
        int databaseSizeBeforeDelete = refreshTokenRepository.findAll().size();

        // Get the refreshToken
        restRefreshTokenMockMvc.perform(delete("/api/refresh-tokens/{id}", refreshToken.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean refreshTokenExistsInEs = refreshTokenSearchRepository.exists(refreshToken.getId());
        assertThat(refreshTokenExistsInEs).isFalse();

        // Validate the database is empty
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRefreshToken() throws Exception {
        // Initialize the database
        refreshTokenRepository.saveAndFlush(refreshToken);
        refreshTokenSearchRepository.save(refreshToken);

        // Search the refreshToken
        restRefreshTokenMockMvc.perform(get("/api/_search/refresh-tokens?query=id:" + refreshToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(refreshToken.getId().intValue())))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN.toString())))
            .andExpect(jsonPath("$.[*].expired").value(hasItem(DEFAULT_EXPIRED.booleanValue())));
    }
}
