package com.nerdery.jhipster.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nerdery.jhipster.security.jwt.JWTConfigurer;
import com.nerdery.jhipster.security.jwt.TokenProvider;
import com.nerdery.jhipster.service.RefreshTokenService;
import com.nerdery.jhipster.service.UserService;
import com.nerdery.jhipster.web.rest.dto.LoginDTO;
import com.nerdery.jhipster.web.rest.dto.RefreshDTO;
import com.nerdery.jhipster.web.rest.util.HeaderUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserJWTController {

    private Logger log = LoggerFactory.getLogger(UserJWTController.class);

    @Inject
    private TokenProvider tokenProvider;

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private RefreshTokenService refreshTokenService;

    @Inject
    private UserService userService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<?> authorize(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.createToken(authentication);
            String refreshJwt = tokenProvider.createRefreshToken(authentication);

            response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, JWTConfigurer.AUTHORIZATION_BEARER_HEADER + jwt);
            response.addHeader(JWTConfigurer.REFRESH_TOKEN_HEADER, refreshJwt);
            return ResponseEntity.ok(new JWTToken(jwt, refreshJwt));
        } catch (AuthenticationException exception) {
            return new ResponseEntity<>(exception.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<?> refresh(@RequestBody RefreshDTO refreshToken, HttpServletResponse response) {
        String token = refreshToken.getRefreshToken();
        try {
            if (tokenProvider.canNewTokenBeIssued(token)) {
                String refreshedToken = tokenProvider.refreshToken(token);

                response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, JWTConfigurer.AUTHORIZATION_BEARER_HEADER + refreshedToken);
                response.addHeader(JWTConfigurer.REFRESH_TOKEN_HEADER, token);
                return ResponseEntity.ok(new JWTToken(refreshedToken, token));
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (ExpiredJwtException eje) {
            log.info("Refresh exception for user {} - {}", eje.getClaims().getSubject(), eje.getMessage());
            HttpHeaders responseHeaders = HeaderUtil.createFailureAlert("refresh", "refreshExpired", "Default message");
            return new ResponseEntity<>(responseHeaders, HttpStatus.UNAUTHORIZED);
        }
    }
}
