package com.nerdery.jhipster.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A RefreshToken.
 */
@Entity
@Table(name = "refresh_token")
@Document(indexName = "refreshtoken")
public class RefreshToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expired")
    private Boolean expired;

    @ManyToOne
    private User user;

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

    public Boolean isExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RefreshToken refreshToken = (RefreshToken) o;
        if(refreshToken.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, refreshToken.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
            "id=" + id +
            ", token='" + token + "'" +
            ", expired='" + expired + "'" +
            '}';
    }
}
