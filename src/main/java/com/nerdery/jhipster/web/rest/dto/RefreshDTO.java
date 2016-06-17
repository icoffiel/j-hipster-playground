package com.nerdery.jhipster.web.rest.dto;

/**
 * A DTO for a refresh request.
 */
public class RefreshDTO {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
