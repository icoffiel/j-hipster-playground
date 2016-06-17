package com.nerdery.jhipster.security.jwt;

import com.nerdery.jhipster.config.JHipsterProperties;
import com.nerdery.jhipster.domain.User;
import com.nerdery.jhipster.service.RefreshTokenService;
import com.nerdery.jhipster.service.UserService;
import com.nerdery.jhipster.web.rest.dto.RefreshTokenDTO;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private String secretKey;

    private long tokenValidityInSeconds;

    private long tokenValidityInSecondsForRememberMe;

    @Inject
    private JHipsterProperties jHipsterProperties;

    @Inject
    private RefreshTokenService refreshTokenService;

    @Inject
    private UserService userService;

    @PostConstruct
    public void init() {
        this.secretKey =
            jHipsterProperties.getSecurity().getAuthentication().getJwt().getSecret();

        this.tokenValidityInSeconds =
            1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
        this.tokenValidityInSecondsForRememberMe =
            1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();
    }

    public String createToken(Authentication authentication) {
        return createToken(authentication, false);
    }

    public String createToken(Authentication authentication, Boolean isRefreshToken) {
        String authorities = authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .collect(Collectors.joining(","));

        return Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setExpiration(getTokenValidity(isRefreshToken))
            .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();

        String principal = claims.getSubject();

        Collection<? extends GrantedAuthority> authorities =
            Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(",")).stream()
                .map(authority -> new SimpleGrantedAuthority(authority))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a new token can be issued using a refresh token
     *
     * @param token The refresh token to check to ensure that a new token can be issues.
     * @return true if a new token can be issued, otherwise false.
     */
    public boolean canNewTokenBeIssued(String token) {
        // TODO - Check against the saved tokens for the user to determine if a token can be sent
        boolean canNewTokenBeIssued = false;

        try {
            canNewTokenBeIssued = isRefreshTokenValid(token) || validateToken(token);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token: " + e.getMessage());
        }

        return canNewTokenBeIssued;
    }

    /**
     * Checks to see if the refresh token is valid.
     *
     * @param token The refresh token to check.
     * @return true if the refresh token was found and is not disabled, otherwise false.
     */
    private boolean isRefreshTokenValid(String token) {
        RefreshTokenDTO refreshToken = refreshTokenService.findOneByToken(token);

        if (refreshToken != null && !refreshToken.getExpired()) {
            return true;
        } else {
            if (refreshToken == null) {
                log.info("No JWT Token found");
            } else {
                log.info("Disabled JWT Token: " + refreshToken.getId());
            }
            return false;
        }
    }

    /**
     * Builds a new token using the refresh token.
     *
     * @param token The refresh token used to generate the new token.
     * @return The new token.
     */
    public String refreshToken(String token) {
        Claims tokenBody = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
        return Jwts.builder()
            .setClaims(tokenBody)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setExpiration(getTokenValidity(false))
            .compact();
    }

    /**
     * Return the date that the token is valid until.
     *
     * @return The {@link Date} that the token should expire on.
     */
    private Date getTokenValidity(Boolean isRefreshToken) {
        long now = (new Date()).getTime();
        Date validity;
        if (isRefreshToken) {
            validity = new Date(now + this.tokenValidityInSecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInSeconds);
        }
        return validity;
    }

    /**
     * Create and save a refresh token. The token will be created and saved to the DB. It will be associated with the current user.
     *
     * @param authentication The authentication principal.
     * @return The created refresh token.
     */
    public String createRefreshToken(Authentication authentication) {
        String refreshToken = createToken(authentication, true);

        User user = userService.getUserWithAuthorities();
        persistRefreshToken(refreshToken, user.getId());

        return refreshToken;
    }

    /**
     * Save the refresh token to the database and associate with the current user.
     *
     * @param refreshJwt The refresh token to persist.
     * @param id The user ID to associate the token to.
     */
    public void persistRefreshToken(String refreshJwt, Long id) {
        // Save the created refresh token
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
        refreshTokenDTO.setToken(refreshJwt);
        refreshTokenDTO.setExpired(false);
        refreshTokenDTO.setUserId(id);
        refreshTokenService.save(refreshTokenDTO);
    }
}
