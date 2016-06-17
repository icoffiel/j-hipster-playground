package com.nerdery.jhipster.repository;

import com.nerdery.jhipster.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the RefreshToken entity.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    @Query("select refreshToken from RefreshToken refreshToken where refreshToken.user.login = ?#{principal.username}")
    List<RefreshToken> findByUserIsCurrentUser();

    RefreshToken findByToken(String token);

}
