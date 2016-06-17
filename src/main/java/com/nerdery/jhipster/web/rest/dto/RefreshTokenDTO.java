package com.nerdery.jhipster.web.rest.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the RefreshToken entity.
 */
public class RefreshTokenDTO implements Serializable {

    private Long id;

    @NotNull
    private String token;


    private Boolean expired;


    private Long userId;

    private String userEmail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RefreshTokenDTO refreshTokenDTO = (RefreshTokenDTO) o;

        if ( ! Objects.equals(id, refreshTokenDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RefreshTokenDTO{" +
            "id=" + id +
            ", token='" + token + "'" +
            ", expired='" + expired + "'" +
            '}';
    }
}
