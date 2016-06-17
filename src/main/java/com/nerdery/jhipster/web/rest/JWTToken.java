package com.nerdery.jhipster.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object to return as body in JWT Authentication
 */
public class JWTToken {
    private String idToken;
    private String refreshToken;

    public JWTToken(String idToken, String refreshJwt) {
        this.idToken = idToken;
        this.refreshToken = refreshJwt;
    }

    @JsonProperty("id_token")
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @JsonProperty("refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
